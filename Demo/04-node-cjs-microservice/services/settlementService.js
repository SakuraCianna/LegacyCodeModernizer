// Batch Settlement Service using callback iterations
const transactionDAO = require('../models/transactionDAO');
const logger = require('../utils/logger');

function processBatchSettlement(transactionIds, callback) {
  if (!Array.isArray(transactionIds) || transactionIds.length === 0) {
    return callback(new Error('EMPTY_TRANSACTION_IDS'));
  }

  const results = {
    settled: [],
    failed: [],
    totalSettledAmount: 0.0,
    totalFees: 0.0
  };

  let index = 0;

  function processNext() {
    if (index >= transactionIds.length) {
      logger.logInfo(`Batch settlement complete. Settled count: ${results.settled.length}`, function() {
        return callback(null, results);
      });
      return;
    }

    const txId = transactionIds[index];
    index++;

    transactionDAO.findById(txId, function(err, tx) {
      if (err || !tx) {
        results.failed.push({ id: txId, reason: err ? err.message : 'NOT_FOUND' });
        return processNext();
      }

      if (tx.status !== 'AUTHORIZED') {
        results.failed.push({ id: txId, reason: 'INVALID_STATUS_' + tx.status });
        return processNext();
      }

      transactionDAO.updateStatus(txId, 'SETTLED', function(updateErr, updatedTx) {
        if (updateErr) {
          results.failed.push({ id: txId, reason: updateErr.message });
        } else {
          results.settled.push(updatedTx);
          results.totalSettledAmount += updatedTx.amount;
          results.totalFees += updatedTx.fee;
        }
        processNext();
      });
    });
  }

  processNext();
}

module.exports = {
  processBatchSettlement: processBatchSettlement
};
