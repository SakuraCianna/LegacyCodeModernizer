// Legacy Payment Processor using crypto callbacks, fs persistence, and error-first callbacks
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');
const logger = require('../utils/logger');

const STORAGE_FILE = path.join(__dirname, '../transactions.json');

function processTransaction(paymentData, secretKey, callback) {
  logger.logInfo(`Processing payment for order: ${paymentData.orderId}`, function(logErr) {
    if (logErr) return callback(logErr);

    if (!paymentData.orderId || !paymentData.amount || paymentData.amount <= 0) {
      return callback(new Error('INVALID_PAYMENT_DATA'));
    }

    // Nested Crypto Hash PBKDF2 Callback
    crypto.pbkdf2(paymentData.orderId, secretKey, 1000, 32, 'sha256', function(err, derivedKey) {
      if (err) return callback(err);

      const record = {
        id: 'TX-' + Math.floor(Math.random() * 1000000),
        orderId: paymentData.orderId,
        amount: paymentData.amount,
        currency: paymentData.currency || 'USD',
        signature: derivedKey.toString('hex'),
        status: 'CONFIRMED',
        createdAt: new Date().toISOString()
      };

      // Nested File Read / Write Pyramid
      fs.readFile(STORAGE_FILE, 'utf8', function(readErr, fileContent) {
        let history = [];
        if (!readErr && fileContent) {
          try {
            history = JSON.parse(fileContent);
          } catch (e) {
            history = [];
          }
        }
        history.push(record);

        fs.writeFile(STORAGE_FILE, JSON.stringify(history, null, 2), 'utf8', function(writeErr) {
          if (writeErr) return callback(writeErr);

          logger.logInfo(`Payment confirmed and persisted: ${record.id}`, function() {
            callback(null, record);
          });
        });
      });
    });
  });
}

module.exports = {
  processTransaction: processTransaction
};
