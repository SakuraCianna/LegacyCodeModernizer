// Express route for charge authorization
const express = require('express');
const router = express.Router();
const chargeService = require('../services/chargeService');
const signatureValidator = require('../middlewares/signatureValidator');
const idempotency = require('../middlewares/idempotency');
const rateLimiter = require('../middlewares/rateLimiter');

router.post('/', rateLimiter, idempotency, signatureValidator, function(req, res) {
  chargeService.processCharge(req.body, function(err, result) {
    if (err) {
      const status = err.message.startsWith('INVALID') ? 400 : 500;
      return res.status(status).json({
        error: 'CHARGE_FAILED',
        message: err.message
      });
    }

    return res.status(201).json({
      status: 'SUCCESS',
      transaction: result
    });
  });
});

module.exports = router;
