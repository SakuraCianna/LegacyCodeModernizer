// Legacy CommonJS Express 4 entrypoint
const express = require('express');
const bodyParser = require('body-parser');
const paymentsRouter = require('./routes/payments');
const logger = require('./utils/logger');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use('/api/payments', paymentsRouter);

// Legacy Error Handler
app.use(function(err, req, res, next) {
  logger.logError('Unhandled Server Exception', err, function() {
    res.status(500).json({ error: 'INTERNAL_SERVER_ERROR' });
  });
});

app.listen(PORT, function() {
  logger.logInfo(`Legacy Node.js CJS Payment Service running on port ${PORT}`);
});
