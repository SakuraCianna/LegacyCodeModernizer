// In-memory sliding window rate limiter middleware (Anti-Pattern: in-memory state in single Node process)
const config = require('../config/default');

const requestWindows = {};

function rateLimiter(req, res, next) {
  const ip = req.ip || req.connection.remoteAddress || '127.0.0.1';
  const now = Date.now();
  const windowMs = config.security.rateLimitWindowMs;
  const maxLimit = config.security.rateLimitMax;

  if (!requestWindows[ip]) {
    requestWindows[ip] = [];
  }

  // Remove timestamps outside current window
  requestWindows[ip] = requestWindows[ip].filter(function(timestamp) {
    return (now - timestamp) < windowMs;
  });

  if (requestWindows[ip].length >= maxLimit) {
    res.setHeader('Retry-After', Math.ceil(windowMs / 1000));
    return res.status(429).json({
      error: 'TOO_MANY_REQUESTS',
      message: 'Rate limit exceeded. Please throttle your transactions.'
    });
  }

  requestWindows[ip].push(now);
  next();
}

module.exports = rateLimiter;
