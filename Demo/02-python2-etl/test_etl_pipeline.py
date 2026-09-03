# -*- coding: utf-8 -*-
"""
Legacy Unit Test Suite for Python 2.7 Financial ETL & Risk Pipeline.
Ensures behavior contracts are testable before and after modernizer transformations.
"""
import unittest
import math
from currency_converter import CurrencyConverter
from risk_engine import FinancialRiskEngine
from rate_limiter import TokenBucketRateLimiter

class TestFinancialPipeline(unittest.TestCase):

    def setUp(self):
        self.converter = CurrencyConverter({
            "USD": 1.0,
            "EUR": 1.10,
            "GBP": 1.25
        })
        self.risk_engine = FinancialRiskEngine(converter=self.converter)

    def test_currency_conversion(self):
        # 100 EUR -> 110 USD
        usd_val = self.converter.convert(100.0, "EUR", "USD")
        self.assertEqual(usd_val, 110.0)

        # 100 USD -> 80 GBP (100 / 1.25)
        gbp_val = self.converter.convert(100.0, "USD", "GBP")
        self.assertEqual(gbp_val, 80.0)

    def test_negative_amount_raises_error(self):
        with self.assertRaises(ValueError):
            self.converter.convert(-50.0, "USD", "EUR")

    def test_volatility_calculation(self):
        returns = [0.01, -0.01, 0.02, -0.02]
        vol = self.risk_engine.calculate_volatility(returns)
        self.assertTrue(vol > 0.0)
        self.assertTrue(vol < 0.05)

    def test_zero_division_protection(self):
        vol = self.risk_engine.calculate_volatility([])
        self.assertEqual(vol, 0.0)

        vol_single = self.risk_engine.calculate_volatility([0.05])
        self.assertEqual(vol_single, 0.0)

    def test_account_risk_evaluation_critical_tier(self):
        positions = [
            {
                "symbol": "BTC/USD",
                "notional": 500000.0,
                "currency": "USD",
                "collateral": 50000.0, # 10x leverage
                "returns": [0.05, -0.06, 0.08, -0.09]
            }
        ]
        result = self.risk_engine.evaluate_account_risk("ACC-TEST-1", positions)
        self.assertEqual(result["account_id"], "ACC-TEST-1")
        self.assertEqual(result["risk_tier"], "CRITICAL_RISK")
        self.assertEqual(result["leverage_ratio"], 10.0)
        self.assertTrue(result["var_95_usd"] > 0)

    def test_rate_limiter_acquire(self):
        limiter = TokenBucketRateLimiter(rate_per_sec=100.0, capacity=10.0)
        self.assertTrue(limiter.acquire(5.0))
        self.assertTrue(limiter.acquire(5.0))
        self.assertFalse(limiter.acquire(5.0, blocking=False))

if __name__ == "__main__":
    unittest.main()
