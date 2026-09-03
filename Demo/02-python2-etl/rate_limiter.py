# -*- coding: utf-8 -*-
"""
Thread-safe Token Bucket Rate Limiter for Python 2.7.
"""
import time
import threading

class TokenBucketRateLimiter(object):
    def __init__(self, rate_per_sec=10.0, capacity=20.0):
        self.rate = float(rate_per_sec)
        self.capacity = float(capacity)
        self.tokens = float(capacity)
        self.last_fill = time.time()
        self.lock = threading.Lock()

    def acquire(self, tokens=1.0, blocking=True):
        with self.lock:
            while True:
                now = time.time()
                elapsed = now - self.last_fill
                self.tokens = min(self.capacity, self.tokens + elapsed * self.rate)
                self.last_fill = now

                if self.tokens >= tokens:
                    self.tokens -= tokens
                    return True

                if not blocking:
                    return False

                sleep_time = (tokens - self.tokens) / self.rate
                time.sleep(max(0.01, sleep_time))
