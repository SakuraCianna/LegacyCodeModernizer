// In-memory idempotency cache with TTL expiration
const config = require('../config/default');

const idempotencyCache = {};

function idempotencyGuard(req, res, next) {
  // Only guard non-safe methods (POST, PUT, PATCH)
  if (req.method === 'GET' || req.method === 'OPTIONS') {
    return next();
  }

  const key = req.headers['x-idempotency-key'];
  if (!key) {
    return res.status(400).json({
      error: 'MISSING_IDEMPOTENCY_KEY',
      message: 'Header X-Idempotency-Key is mandatory for transactional operations'
    });
  }

  const now = Date.now();
  const cached = idempotencyCache[key];

  if (cached) {
    if (now - cached.timestamp < config.security.tokenTtlMs) {
      // Return cached response directly without re-executing
      return res.status(cached.statusCode).json(cached.body);
    } else {
      delete idempotencyCache[key];
    }
  }

  // Intercept res.json to capture response
  const originalJson = res.json.bind(res);
  res.json = function(body) {
    if (res.statusCode >= 200 && res.statusCode < 300) {
      idempotencyCache[key] = {
        timestamp: Date.now(),
        statusCode: res.statusCode,
        body: body
      };
    }
    return originalJson(body);
  };

  next();
}

module.exports = idempotencyGuard;
