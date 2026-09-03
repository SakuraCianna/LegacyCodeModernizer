# -*- coding: utf-8 -*-
"""
Legacy Risk Engine for financial portfolio volatility, VaR (Value at Risk), and leverage scoring.
"""
import math
import types
from currency_converter import CurrencyConverter

class FinancialRiskEngine(object):
    def __init__(self, converter=None, risk_free_rate=0.045):
        self.converter = converter if converter else CurrencyConverter()
        self.risk_free_rate = risk_free_rate

    def calculate_volatility(self, return_series):
        """
        Computes standard deviation of returns with zero-division protection.
        """
        if not return_series or len(return_series) < 2:
            return 0.0

        n = len(return_series)
        mean = sum(return_series) / float(n)
        variance = sum([(r - mean) ** 2 for r in return_series]) / float(n - 1)
        return math.sqrt(variance)

    def calculate_sharpe_ratio(self, returns):
        vol = self.calculate_volatility(returns)
        if vol <= 0.000001:
            return 0.0
        annualized_mean = (sum(returns) / float(len(returns))) * 252.0
        annualized_vol = vol * math.sqrt(252.0)
        return (annualized_mean - self.risk_free_rate) / annualized_vol

    def evaluate_account_risk(self, account_id, positions):
        """
        positions is a list of dicts:
        [{"symbol": "AAPL", "notional": 50000, "currency": "USD", "collateral": 15000, "returns": [0.01, -0.02, 0.015]}]
        """
        total_usd_exposure = 0.0
        total_usd_collateral = 0.0
        weighted_volatilities = []

        for pos in positions:
            notional = pos.get("notional", 0.0)
            currency = pos.get("currency", "USD")
            collateral = pos.get("collateral", 0.0)
            returns = pos.get("returns", [])

            usd_notional = self.converter.convert(notional, currency, "USD")
            usd_collateral = self.converter.convert(collateral, currency, "USD")

            total_usd_exposure += usd_notional
            total_usd_collateral += usd_collateral

            vol = self.calculate_volatility(returns)
            weighted_volatilities.append(vol * usd_notional)

        # Fool-proof zero collateral protection
        leverage = total_usd_exposure / max(1.0, total_usd_collateral)
        avg_vol = (sum(weighted_volatilities) / max(1.0, total_usd_exposure)) if total_usd_exposure > 0 else 0.0

        # Parametric VaR (95% confidence Z=1.645)
        var_95_usd = total_usd_exposure * avg_vol * 1.645

        # Risk Classification
        if leverage > 4.0 or avg_vol > 0.40:
            risk_tier = "CRITICAL_RISK"
        elif leverage > 2.5 or avg_vol > 0.25:
            risk_tier = "HIGH_RISK"
        elif leverage > 1.5:
            risk_tier = "MODERATE_RISK"
        else:
            risk_tier = "LOW_RISK"

        return {
            "account_id": str(account_id),
            "total_usd_exposure": round(total_usd_exposure, 2),
            "total_usd_collateral": round(total_usd_collateral, 2),
            "leverage_ratio": round(leverage, 2),
            "portfolio_volatility": round(avg_vol, 4),
            "var_95_usd": round(var_95_usd, 2),
            "risk_tier": risk_tier
        }
