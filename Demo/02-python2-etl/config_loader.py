# -*- coding: utf-8 -*-
"""
Legacy configuration loader using Python 2.7 ConfigParser and raw file handling.
"""
import ConfigParser
import os
import sys
import urllib

class LegacyConfigLoader:
    def __init__(self, config_path="config.ini"):
        self.config_path = config_path
        self.parser = ConfigParser.SafeConfigParser()

    def load_defaults(self):
        defaults = {
            "api_endpoint": urllib.quote("https://api.legacy-finance.internal/v1/feed"),
            "batch_size": "50",
            "retry_limit": "3",
            "log_level": "DEBUG"
        }
        print >> sys.stdout, "Loaded default configurations using urllib.quote"
        return defaults

    def get_api_url(self, base_url, token):
        # Python 2 string formatting and dictionary checking
        params = {"token": token, "format": "json"}
        if params.has_key("token"):
            encoded_params = urllib.urlencode(params)
            return "%s?%s" % (base_url, encoded_params)
        return base_url
