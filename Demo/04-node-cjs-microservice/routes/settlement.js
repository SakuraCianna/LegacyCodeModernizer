// Express route for settlement and reconciliation
const express = require('express');
const router = express.Router();
const settlementService = require('../services/settlementService');
const reconciliation = require('../services/reconciliation');
const rateLimiter = require('../middlewares/rateLimiter');

router.post('/batch', rateLimiter, function(req, res) {
  const txIds = req.body.transactionIds;
  settlementService.processBatchSettlement(txIds, function(err, result) {
    if (err) {
      return res.status(400).json({ error: 'BATCH_SETTLEMENT_ERROR', message: err.message });
    }
    return res.status(200).json({ status: 'SUCCESS', data: result });
  });
});

router.post('/reconcile', rateLimiter, function(req, res) {
  const bankFeed = req.body.bankTransactions || [];
  reconciliation.performReconciliation(bankFeed, function(err, report) {
    if (err) {
      return res.status(500).json({ error: 'RECONCILIATION_ERROR', message: err.message });
    }
    return res.status(200).json({ status: 'SUCCESS', report: report });
  });
});

module.exports = router;
