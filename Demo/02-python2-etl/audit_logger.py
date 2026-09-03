# -*- coding: utf-8 -*-
"""
Audit Logger with cross-thread file lock and timestamp tracking.
"""
import os
import time
import threading
import json

class AuditTrailLogger(object):
    def __init__(self, log_filepath="./data/audit_execution.log"):
        self.log_filepath = log_filepath
        self.lock = threading.Lock()
        log_dir = os.path.dirname(self.log_filepath)
        if log_dir and not os.path.exists(log_dir):
            try:
                os.makedirs(log_dir)
            except OSError:
                pass

    def log_event(self, event_type, account_id, details):
        timestamp = time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime())
        record = {
            "timestamp": timestamp,
            "event_type": str(event_type),
            "account_id": str(account_id),
            "details": details
        }
        line = json.dumps(record) + "\n"

        with self.lock:
            with open(self.log_filepath, "a") as f:
                f.write(line)
                f.flush()
