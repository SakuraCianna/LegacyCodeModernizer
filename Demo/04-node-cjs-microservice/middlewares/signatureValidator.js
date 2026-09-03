// HMAC Signature verification middleware
const cryptoHelper = require('../utils/cryptoHelper');
const config = require('../config/default');

function signatureValidator(req, res, next) {
  const signature = req.headers['x-signature-sha256'];
  if (!signature) {
    return res.status(401).json({
      error: 'UNAUTHORIZED',
      message: 'Missing required X-Signature-SHA256 authorization signature'
    });
  }

  cryptoHelper.verifySignature(req.body, signature, config.security.apiSecret, function(err, isValid) {
    if (err || !isValid) {
      return res.status(403).json({
        error: 'INVALID_SIGNATURE',
        message: 'Signature mismatch or tamper detected'
      });
    }
    next();
  });
}

module.exports = signatureValidator;
