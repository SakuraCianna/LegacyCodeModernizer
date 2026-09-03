// Legacy Express 4 router with callback chaining
const express = require('express');
const router = express.Router();
const paymentProcessor = require('../services/paymentProcessor');
const logger = require('../utils/logger');

const API_SECRET = 'legacy-system-jwt-secret-key-2022';

router.post('/charge', function(req, res) {
  const paymentData = req.body;

  paymentProcessor.processTransaction(paymentData, API_SECRET, function(err, result) {
    if (err) {
      logger.logError('Payment processing failed', err, function() {
        return res.status(400).json({
          status: 'ERROR',
          message: err.message
        });
      });
      return;
    }

    return res.status(200).json({
      status: 'SUCCESS',
      transaction: result
    });
  });
});

module.exports = router;
