# -*- coding: utf-8 -*-
"""
Legacy HTTP Client using Python 2.7 urllib2, cookielib, and exponential backoff.
"""
import urllib2
import cookielib
import json
import time
import sys
from rate_limiter import TokenBucketRateLimiter

class LegacyFinancialHttpClient(object):
    def __init__(self, rate_limiter=None, max_retries=3, backoff_factor=1.5):
        self.rate_limiter = rate_limiter if rate_limiter else TokenBucketRateLimiter(15.0, 30.0)
        self.max_retries = max_retries
        self.backoff_factor = backoff_factor

        # Setup cookie jar and custom urllib2 opener
        self.cookie_jar = cookielib.CookieJar()
        self.opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(self.cookie_jar))

    def get_json(self, url, custom_headers=None):
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) LegacyFinEngine/2.7',
            'Accept': 'application/json',
            'Connection': 'keep-alive'
        }
        if custom_headers and isinstance(custom_headers, dict):
            for k, v in custom_headers.iteritems():
                headers[str(k)] = str(v)

        for attempt in xrange(self.max_retries):
            # Rate limiting gate
            self.rate_limiter.acquire(1.0)
            try:
                req = urllib2.Request(url, headers=headers)
                response = self.opener.open(req, timeout=8)
                raw_payload = response.read()
                return json.loads(raw_payload)
            except urllib2.HTTPError, http_err:
                print >> sys.stderr, "[HTTP %d] Attempt %d failed for %s: %s" % (
                    http_err.code, attempt + 1, url, http_err.reason
                )
                if http_err.code in (400, 401, 403, 404):
                    # Unrecoverable errors
                    raise
            except urllib2.URLError, url_err:
                print >> sys.stderr, "[URLError] Attempt %d failed: %s" % (attempt + 1, str(url_err.reason))
            except Exception, general_err:
                print >> sys.stderr, "[Unknown Error] Attempt %d: %s" % (attempt + 1, str(general_err))

            sleep_duration = self.backoff_factor * (2 ** attempt)
            time.sleep(sleep_duration)

        raise RuntimeError("Exceeded maximum retries (%d) for URL: %s" % (self.max_retries, url))
