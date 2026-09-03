# -*- coding: utf-8 -*-
"""
Multi-threaded Queue consumer and batch aggregation engine in Python 2.7.
"""
import Queue
import threading
import time
import sys
from risk_engine import FinancialRiskEngine

class BatchAggregatorWorker(threading.Thread):
    def __init__(self, work_queue, results_list, risk_engine, worker_id=1):
        super(BatchAggregatorWorker, self).__init__()
        self.work_queue = work_queue
        self.results_list = results_list
        self.risk_engine = risk_engine
        self.worker_id = worker_id
        self.daemon = True

    def run(self):
        while True:
            try:
                batch = self.work_queue.get(timeout=2.0)
                if batch is None: # Poison pill sentinel
                    self.work_queue.task_done()
                    break

                for account_record in batch:
                    acc_id = account_record.get("account_id")
                    positions = account_record.get("positions", [])
                    risk_summary = self.risk_engine.evaluate_account_risk(acc_id, positions)
                    self.results_list.append(risk_summary)

                self.work_queue.task_done()
            except Queue.Empty:
                break
            except Exception, err:
                print >> sys.stderr, "[Worker %d Error] %s" % (self.worker_id, str(err))
                self.work_queue.task_done()

class BatchPipelineCoordinator(object):
    def __init__(self, concurrency=4):
        self.concurrency = concurrency
        self.queue = Queue.Queue()
        self.risk_engine = FinancialRiskEngine()

    def process_all_accounts(self, account_records, chunk_size=20):
        results = []
        # Chunking with Python 2 xrange
        total_chunks = (len(account_records) + chunk_size - 1) / chunk_size
        for i in xrange(total_chunks):
            chunk = account_records[i * chunk_size : (i + 1) * chunk_size]
            self.queue.put(chunk)

        workers = []
        for w_id in xrange(self.concurrency):
            w = BatchAggregatorWorker(self.queue, results, self.risk_engine, worker_id=w_id + 1)
            w.start()
            workers.append(w)

        self.queue.join()

        # Send stop sentinels
        for _ in workers:
            self.queue.put(None)

        for w in workers:
            w.join(timeout=3.0)

        return results
