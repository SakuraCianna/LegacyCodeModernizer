# -*- coding: utf-8 -*-
"""
Legacy Storage Handler using cPickle binary persistence, CSV dumps, and file path manipulation.
"""
import cPickle
import csv
import os
import sys

class LegacyStorageHandler(object):
    def __init__(self, pickle_path="./data/risk_state.pkl", csv_path="./data/risk_summary.csv"):
        self.pickle_path = pickle_path
        self.csv_path = csv_path
        self._ensure_dir()

    def _ensure_dir(self):
        d = os.path.dirname(self.pickle_path)
        if d and not os.path.exists(d):
            os.makedirs(d)

    def save_pickle_state(self, state_dict):
        try:
            with open(self.pickle_path, "wb") as f:
                cPickle.dump(state_dict, f, protocol=2)
            return True
        except Exception, e:
            print >> sys.stderr, "Failed to save pickle state: %s" % str(e)
            return False

    def load_pickle_state(self):
        if not os.path.exists(self.pickle_path):
            return {}
        try:
            with open(self.pickle_path, "rb") as f:
                return cPickle.load(f)
        except Exception, e:
            print >> sys.stderr, "Failed to load pickle state: %s" % str(e)
            return {}

    def export_csv_summary(self, risk_records):
        if not risk_records:
            return False

        fieldnames = ["account_id", "total_usd_exposure", "total_usd_collateral", "leverage_ratio", "portfolio_volatility", "var_95_usd", "risk_tier"]
        try:
            with open(self.csv_path, "wb") as f:
                writer = csv.DictWriter(f, fieldnames=fieldnames)
                writer.writeheader()
                for r in risk_records:
                    writer.writerow(r)
            return True
        except Exception, e:
            print >> sys.stderr, "CSV Export failed: %s" % str(e)
            return False
