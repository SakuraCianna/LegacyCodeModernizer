// Legacy configuration object in CommonJS
module.exports = {
  server: {
    port: process.env.PORT || 3000,
    env: process.env.NODE_ENV || 'production'
  },
  security: {
    apiSecret: 'legacy-fintech-master-secret-2022',
    tokenTtlMs: 300000, // 5 minutes idempotency window
    rateLimitMax: 30,   // max requests per window
    rateLimitWindowMs: 60000 // 1 minute
  },
  storage: {
    transactionsPath: './data/transactions_ledger.json',
    auditLogPath: './data/settlement_audit.log'
  }
};
