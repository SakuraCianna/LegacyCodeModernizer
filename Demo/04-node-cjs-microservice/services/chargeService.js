// Charge service with nested callback pyramid (Anti-Pattern: Callback Hell)
const cryptoHelper = require('../utils/cryptoHelper');
const transactionDAO = require('../models/transactionDAO');
const logger = require('../utils/logger');
const config = require('../config/default');

function processCharge(chargeReq, callback) {
  logger.logInfo(`Processing charge for account: ${chargeReq.accountId}, amount: ${chargeReq.amount}`, function(logErr) {
    if (logErr) return callback(logErr);

    // Validate boundaries (Fool-proof check)
    if (!chargeReq.accountId || !chargeReq.amount || chargeReq.amount <= 0) {
      return callback(new Error('INVALID_CHARGE_AMOUNT_OR_ACCOUNT'));
    }

    // Step 1: Derive security authorization hash
    cryptoHelper.deriveKeyPbkdf2(chargeReq.accountId, config.security.apiSecret, 1000, 16, function(pbkdf2Err, authHash) {
      if (pbkdf2Err) return callback(pbkdf2Err);

      // Step 2: Compute Transaction Payload HMAC
      const txPayload = {
        id: 'TXN-' + Date.now() + '-' + Math.floor(Math.random() * 10000),
        accountId: chargeReq.accountId,
        merchantId: chargeReq.merchantId || 'MERC-GLOBAL-01',
        amount: Number(chargeReq.amount),
        currency: chargeReq.currency || 'USD',
        authHash: authHash,
        status: 'AUTHORIZED',
        fee: Math.round(Number(chargeReq.amount) * 0.029 * 100) / 100 + 0.30, // 2.9% + $0.30
        createdAt: new Date().toISOString()
      };

      cryptoHelper.computeHmacSignature(txPayload, config.security.apiSecret, function(hmacErr, signature) {
        if (hmacErr) return callback(hmacErr);

        txPayload.signature = signature;

        // Step 3: Persist transaction in DAO
        transactionDAO.save(txPayload, function(saveErr, savedTx) {
          if (saveErr) return callback(saveErr);

          logger.logInfo(`Charge successfully authorized and saved: ${savedTx.id}`, function() {
            callback(null, savedTx);
          });
        });
      });
    });
  });
}

module.exports = {
  processCharge: processCharge
};
