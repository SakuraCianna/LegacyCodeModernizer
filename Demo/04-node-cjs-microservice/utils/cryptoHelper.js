// Legacy Crypto Helper using callback pyramids and old Buffer APIs
const crypto = require('crypto');

function computeHmacSignature(payload, secretKey, callback) {
  if (!payload || !secretKey) {
    return callback(new Error('PAYLOAD_OR_SECRET_MISSING'));
  }
  try {
    const rawData = typeof payload === 'object' ? JSON.stringify(payload) : String(payload);
    const hmac = crypto.createHmac('sha256', secretKey);
    hmac.update(rawData, 'utf8');
    const signature = hmac.digest('hex');
    return callback(null, signature);
  } catch (err) {
    return callback(err);
  }
}

function deriveKeyPbkdf2(password, salt, iterations, keyLength, callback) {
  crypto.pbkdf2(password, salt, iterations || 1000, keyLength || 32, 'sha256', function(err, key) {
    if (err) return callback(err);
    callback(null, key.toString('hex'));
  });
}

function verifySignature(payload, expectedSig, secretKey, callback) {
  computeHmacSignature(payload, secretKey, function(err, computedSig) {
    if (err) return callback(err);
    const isValid = (computedSig === expectedSig);
    callback(null, isValid);
  });
}

module.exports = {
  computeHmacSignature: computeHmacSignature,
  deriveKeyPbkdf2: deriveKeyPbkdf2,
  verifySignature: verifySignature
};
