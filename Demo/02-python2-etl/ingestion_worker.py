# -*- coding: utf-8 -*-
"""
Main Ingestion Worker Orchestrator in Python 2.7.
Connects config loading, HTTP polling, rate limiting, multi-threaded batch risk calculation,
audit logging, and persistence.
"""
import os
import sys
import json
import time

from config_loader import LegacyConfigLoader
from http_client import LegacyFinancialHttpClient
from batch_aggregator import BatchPipelineCoordinator
from audit_logger import AuditTrailLogger
from storage_handler import LegacyStorageHandler

class IngestionWorker(object):
    def __init__(self, config_path="config.ini"):
        self.config = LegacyConfigLoader(config_path)
        self.http_client = LegacyFinancialHttpClient()
        self.coordinator = BatchPipelineCoordinator(concurrency=self.config.get_int("DEFAULT", "max_workers", 4))
        self.audit = AuditTrailLogger()
        self.storage = LegacyStorageHandler()

    def run_pipeline(self, local_mock_feed=None):
        print >> sys.stdout, "=========================================================="
        print >> sys.stdout, "Starting Python 2.7 Financial ETL & Risk Pipeline"
        print >> sys.stdout, "=========================================================="

        start_time = time.time()
        records = []

        if local_mock_feed and os.path.exists(local_mock_feed):
            print "Loading local mock dataset from: %s" % local_mock_feed
            with open(local_mock_feed, "r") as f:
                records = json.load(f)
        else:
            # Polling remote feed via rate-limited urllib2
            try:
                endpoint = self.config.get_api_endpoint("ALL_ACCOUNTS")
                records = self.http_client.get_json(endpoint)
            except Exception, e:
                print >> sys.stderr, "Remote fetch failed, fallback to embedded sample: %s" % str(e)
                records = [
                    {"account_id": "ACC-FB-1", "positions": [{"symbol": "ETH/USD", "notional": 50000.0, "currency": "USD", "collateral": 15000.0, "returns": [0.03, -0.02]}]}
                ]

        print "Total accounts to evaluate: %d" % len(records)
        self.audit.log_event("PIPELINE_START", "SYSTEM", {"accounts_count": len(records)})

        # Multi-threaded batch evaluation
        summaries = self.coordinator.process_all_accounts(records)

        # Logging audit events per high-risk account
        for s in summaries:
            tier = s.get("risk_tier")
            acc = s.get("account_id")
            if tier in ("HIGH_RISK", "CRITICAL_RISK"):
                self.audit.log_event("RISK_ALERT", acc, s)
                print >> sys.stderr, ">> [ALERT] Account %s reached %s (Leverage: %.2fx, VaR95: $%.2f)" % (
                    acc, tier, s.get("leverage_ratio"), s.get("var_95_usd")
                )

        # Persistence to pickle and CSV
        self.storage.save_pickle_state({"last_run": time.time(), "results": summaries})
        self.storage.export_csv_summary(summaries)

        elapsed = time.time() - start_time
        print >> sys.stdout, "ETL Pipeline completed in %.3f seconds. Evaluated: %d accounts." % (elapsed, len(summaries))
        self.audit.log_event("PIPELINE_COMPLETE", "SYSTEM", {"duration_sec": elapsed, "evaluated": len(summaries)})
        return summaries

if __name__ == "__main__":
    mock_data_path = os.path.join(os.path.dirname(__file__), "data", "sample_market_feed.json")
    worker = IngestionWorker()
    worker.run_pipeline(mock_data_path)
