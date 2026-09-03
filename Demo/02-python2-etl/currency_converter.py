# -*- coding: utf-8 -*-
"""
Currency Converter with caching and edge case validation.
"""
import types
import threading

class CurrencyConverter(object):
    def __init__(self, static_rates=None):
        self.lock = threading.RLock()
        self._rates = {
            "USD": 1.0,
            "EUR": 1.0850,
            "GBP": 1.2650,
            "JPY": 0.0067,
            "CNY": 0.1385,
            "AUD": 0.6550
        }
        if static_rates and isinstance(static_rates, dict):
            self._rates.update(static_rates)

    def convert(self, amount, from_curr, to_curr="USD"):
        # Boundary and type assertions (Fool-proof check)
        if amount is None or amount < 0:
            raise ValueError("Amount cannot be negative or None, got: %s" % str(amount))

        if type(from_curr) != types.StringType and type(from_curr) != types.UnicodeType:
            raise TypeError("from_curr must be string/unicode, got: %s" % type(from_curr))

        from_key = str(from_curr).strip().upper()
        to_key = str(to_curr).strip().upper()

        with self.lock:
            if not self._rates.has_key(from_key):
                raise KeyError("Unsupported source currency: %s" % from_key)
            if not self._rates.has_key(to_key):
                raise KeyError("Unsupported target currency: %s" % to_key)

            usd_equivalent = float(amount) * self._rates[from_key]
            converted = usd_equivalent / self._rates[to_key]
            return round(converted, 4)

    def update_rate(self, currency, new_rate):
        with self.lock:
            curr = str(currency).strip().upper()
            if new_rate <= 0:
                raise ValueError("Exchange rate must be positive: %f" % new_rate)
            self._rates[curr] = float(new_rate)
