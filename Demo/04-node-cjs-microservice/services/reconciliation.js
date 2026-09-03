// Financial Reconciliation Service
const transactionDAO = require('../models/transactionDAO');

function performReconciliation(externalBankFeed, callback) {
  transactionDAO.findAll(function(err, internalLedger) {
    if (err) return callback(err);

    const report = {
      matched: [],
      missingInBank: [],
      missingInLedger: [],
      amountDiscrepancies: []
    };

    const internalMap = {};
    internalLedger.forEach(function(tx) {
      internalMap[tx.id] = tx;
    });

    const bankMap = {};
    externalBankFeed.forEach(function(bankTx) {
      bankMap[bankTx.referenceId] = bankTx;

      const internalMatch = internalMap[bankTx.referenceId];
      if (!internalMatch) {
        report.missingInLedger.push(bankTx);
      } else if (Math.abs(internalMatch.amount - bankTx.amount) > 0.01) {
        report.amountDiscrepancies.push({
          id: bankTx.referenceId,
          ledgerAmount: internalMatch.amount,
          bankAmount: bankTx.amount,
          delta: internalMatch.amount - bankTx.amount
        });
      } else {
        report.matched.push(bankTx.referenceId);
      }
    });

    internalLedger.forEach(function(tx) {
      if (tx.status === 'SETTLED' && !bankMap[tx.id]) {
        report.missingInBank.push(tx);
      }
    });

    callback(null, report);
  });
}

module.exports = {
  performReconciliation: performReconciliation
};
