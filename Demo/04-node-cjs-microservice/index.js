// Legacy CommonJS Express 4 entrypoint
const express = require('express');
const bodyParser = require('body-parser');
const config = require('./config/default');
const chargeRoute = require('./routes/charge');
const settlementRoute = require('./routes/settlement');
const logger = require('./utils/logger');

const app = express();
const PORT = config.server.port;

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Route Mappings
app.use('/api/v1/charge', chargeRoute);
app.use('/api/v1/settlement', settlementRoute);

// Health Check
app.get('/health', function(req, res) {
  res.status(200).json({ status: 'UP', timestamp: new Date().toISOString() });
});

// Legacy Error-Handling Middleware (Must have 4 parameters)
app.use(function(err, req, res, next) {
  logger.logError('Unhandled Application Error', err, function() {
    res.status(500).json({
      error: 'INTERNAL_SERVER_ERROR',
      message: err.message || 'An unexpected failure occurred'
    });
  });
});

if (require.main === module) {
  app.listen(PORT, function() {
    logger.logInfo(`Legacy Node CJS Payment Gateway running on http://localhost:${PORT}`);
  });
}

module.exports = app;
