// Legacy Unit / Integration Test Suite using Node assert and callback patterns
const assert = require('assert');
const cryptoHelper = require('../utils/cryptoHelper');
const chargeService = require('../services/chargeService');
const settlementService = require('../services/settlementService');
const reconciliation = require('../services/reconciliation');

console.log('Running Demo 04 Payment Service Test Suite...');

// Test 1: HMAC Signature Generation & Verification
cryptoHelper.computeHmacSignature({ accountId: 'ACC-123', amount: 100 }, 'secret-test', function(err, sig) {
  assert.strictEqual(err, null);
  assert.ok(sig && sig.length === 64, 'HMAC SHA256 hex string should be 64 characters');

  cryptoHelper.verifySignature({ accountId: 'ACC-123', amount: 100 }, sig, 'secret-test', function(verErr, isValid) {
    assert.strictEqual(verErr, null);
    assert.strictEqual(isValid, true, 'Signature should verify successfully');
    console.log('✔ Test 1 Passed: Crypto HMAC verification');

    // Test 2: Charge Processing & Fee Calculation
    const chargeReq = {
      accountId: 'ACC-MOCK-99',
      merchantId: 'MERC-TEST-1',
      amount: 200.0,
      currency: 'USD'
    };

    chargeService.processCharge(chargeReq, function(chargeErr, tx) {
      assert.strictEqual(chargeErr, null);
      assert.strictEqual(tx.status, 'AUTHORIZED');
      assert.strictEqual(tx.amount, 200.0);
      assert.strictEqual(tx.fee, 6.1); // 200 * 0.029 + 0.30 = 5.80 + 0.30 = 6.10
      console.log('✔ Test 2 Passed: Charge processing & fee computation');

      // Test 3: Batch Settlement
      settlementService.processBatchSettlement([tx.id], function(settleErr, settleRes) {
        assert.strictEqual(settleErr, null);
        assert.strictEqual(settleRes.settled.length, 1);
        assert.strictEqual(settleRes.totalSettledAmount, 200.0);
        console.log('✔ Test 3 Passed: Batch settlement execution');

        // Test 4: Financial Reconciliation
        const mockBankFeed = [
          { referenceId: tx.id, amount: 200.0, timestamp: '2023-09-01T12:00:00Z' }
        ];

        reconciliation.performReconciliation(mockBankFeed, function(recErr, recReport) {
          assert.strictEqual(recErr, null);
          assert.strictEqual(recReport.matched.length, 1);
          assert.strictEqual(recReport.missingInBank.length, 0);
          assert.strictEqual(recReport.amountDiscrepancies.length, 0);
          console.log('✔ Test 4 Passed: Financial reconciliation ledger match');
          console.log('\nAll Demo 04 Test Cases Passed 100%!');
        });
      });
    });
  });
});
