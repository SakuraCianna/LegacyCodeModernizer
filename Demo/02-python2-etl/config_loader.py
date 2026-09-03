# -*- coding: utf-8 -*-
"""
Legacy Config Loader using Python 2.7 ConfigParser, dict.has_key, and urllib urlencode.
"""
import ConfigParser
import os
import sys
import urllib

class LegacyConfigLoader(object):
    """
    Old-style Python 2 class configuration loader.
    """
    def __init__(self, config_file="config.ini"):
        self.config_file = config_file
        self.parser = ConfigParser.SafeConfigParser()
        self._load_config()

    def _load_config(self):
        if not os.path.exists(self.config_file):
            print >> sys.stderr, "Warning: Config file %s not found, loading in-memory fallback" % self.config_file
            self._set_fallbacks()
        else:
            self.parser.read(self.config_file)

    def _set_fallbacks(self):
        self.parser.add_section("market_api")
        self.parser.set("market_api", "base_url", "https://api.legacy-fintech.internal/v1/quotes")
        self.parser.set("market_api", "api_key", "DEFAULT-KEY")
        self.parser.set("market_api", "timeout_sec", "10")
        self.parser.set("market_api", "rate_limit_per_sec", "10")

        self.parser.add_section("risk_thresholds")
        self.parser.set("risk_thresholds", "max_leverage_ratio", "5.0")
        self.parser.set("risk_thresholds", "volatility_warning_limit", "0.35")
        self.parser.set("risk_thresholds", "default_base_currency", "USD")

    def get_api_endpoint(self, symbol, timeframe="1h"):
        base_url = self.parser.get("market_api", "base_url")
        api_key = self.parser.get("market_api", "api_key")

        # Python 2 dict has_key and urllib encoding
        query_params = {
            "symbol": symbol,
            "timeframe": timeframe,
            "api_key": api_key,
            "ts": str(int(os.times()[4]))
        }

        if query_params.has_key("symbol") and query_params.has_key("api_key"):
            encoded_query = urllib.urlencode(query_params)
            return "%s?%s" % (base_url, encoded_query)
        return base_url

    def get_risk_params(self):
        return {
            "max_leverage": float(self.parser.get("risk_thresholds", "max_leverage_ratio")),
            "volatility_limit": float(self.parser.get("risk_thresholds", "volatility_warning_limit")),
            "base_currency": str(self.parser.get("risk_thresholds", "default_base_currency"))
        }

    def get_int(self, section, key, default_val=0):
        try:
            return self.parser.getint(section, key)
        except Exception, err:
            print >> sys.stderr, "Error parsing int [%s] %s: %s" % (section, key, str(err))
            return default_val
