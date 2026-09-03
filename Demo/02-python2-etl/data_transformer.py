# -*- coding: utf-8 -*-
"""
Legacy financial data transformer with Python 2 unicode/str ambiguities,
cPickle usage, and old-style string filtering.
"""
import cPickle
import types

class FinancialTransformer:
    def __init__(self, exchange_rates=None):
        if exchange_rates is None:
            self.exchange_rates = {"USD": 1.0, "EUR": 1.08, "GBP": 1.25}
        else:
            self.exchange_rates = exchange_rates

    def convert_to_usd(self, amount, currency):
        if type(currency) != types.StringType and type(currency) != types.UnicodeType:
            raise TypeError("Currency must be string or unicode, got %s" % type(currency))

        curr_str = str(currency).upper()
        if not self.exchange_rates.has_key(curr_str):
            raise ValueError("Unsupported currency: %s" % curr_str)

        rate = self.exchange_rates[curr_str]
        return float(amount) * rate

    def serialize_state(self, obj):
        # Legacy binary serialization vulnerable to execution in Python 3
        return cPickle.dumps(obj)

    def deserialize_state(self, byte_data):
        return cPickle.loads(byte_data)

    def aggregate_by_customer(self, transaction_records):
        customer_totals = {}
        for record in transaction_records:
            cust_id = record.get("customer_id")
            amt = record.get("amount", 0.0)
            curr = record.get("currency", "USD")
            usd_val = self.convert_to_usd(amt, curr)

            if customer_totals.has_key(cust_id):
                customer_totals[cust_id] += usd_val
            else:
                customer_totals[cust_id] = usd_val
        return customer_totals
