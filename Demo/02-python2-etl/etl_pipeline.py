# -*- coding: utf-8 -*-
"""
Legacy batch ETL pipeline using urllib2, Queue.Queue, xrange, and print statements.
"""
import urllib2
import json
import Queue
import threading
import time
import sys
from data_transformer import FinancialTransformer
from config_loader import LegacyConfigLoader

class LegacyETLPipeline:
    def __init__(self, worker_threads=2):
        self.worker_threads = worker_threads
        self.queue = Queue.Queue()
        self.transformer = FinancialTransformer()
        self.config = LegacyConfigLoader().load_defaults()

    def fetch_remote_feed(self, url):
        try:
            req = urllib2.Request(url, headers={'User-Agent': 'LegacyCrawler/2.7'})
            response = urllib2.urlopen(req, timeout=10)
            data = json.loads(response.read())
            print >> sys.stdout, "Successfully fetched %d records from %s" % (len(data), url)
            return data
        except urllib2.HTTPError, e:
            print >> sys.stderr, "HTTPError encountered: %d - %s" % (e.code, e.reason)
            return []
        except Exception, ex:
            print >> sys.stderr, "Unexpected error fetching feed: %s" % str(ex)
            return []

    def worker(self, results):
        while not self.queue.empty():
            try:
                batch = self.queue.get_nowait()
                aggregated = self.transformer.aggregate_by_customer(batch)
                results.append(aggregated)
                self.queue.task_done()
            except Queue.Empty:
                break

    def run_batch_processing(self, records):
        batch_size = int(self.config.get("batch_size", 2))
        total_batches = (len(records) + batch_size - 1) / batch_size

        # Python 2 xrange loop
        for i in xrange(total_batches):
            chunk = records[i * batch_size : (i + 1) * batch_size]
            self.queue.put(chunk)

        results = []
        threads = []
        for _ in xrange(self.worker_threads):
            t = threading.Thread(target=self.worker, args=(results,))
            t.start()
            threads.append(t)

        for t in threads:
            t.join()

        print "Processed %d batch chunks successfully." % len(results)
        return results

if __name__ == "__main__":
    sample_records = [
        {"customer_id": "CUST-01", "amount": 100.0, "currency": "USD"},
        {"customer_id": "CUST-02", "amount": 200.0, "currency": "EUR"},
        {"customer_id": "CUST-01", "amount": 50.0, "currency": "USD"},
    ]
    pipeline = LegacyETLPipeline()
    final_output = pipeline.run_batch_processing(sample_records)
    print "Final Aggregated Output:", final_output
