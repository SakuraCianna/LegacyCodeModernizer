// Legacy file logger using callback patterns and CommonJS exports
const fs = require('fs');
const path = require('path');

const LOG_FILE = path.join(__dirname, '../app.log');

function logInfo(message, callback) {
  const timestamp = new Date().toISOString();
  const entry = `[${timestamp}] [INFO] ${message}\n`;
  fs.appendFile(LOG_FILE, entry, 'utf8', function(err) {
    if (err) {
      console.error('Failed to write log:', err);
      if (callback) return callback(err);
    }
    console.log(entry.trim());
    if (callback) callback(null);
  });
}

function logError(message, err, callback) {
  const timestamp = new Date().toISOString();
  const entry = `[${timestamp}] [ERROR] ${message} - ${err && err.stack ? err.stack : err}\n`;
  fs.appendFile(LOG_FILE, entry, 'utf8', function(appendErr) {
    if (appendErr) {
      console.error('Failed to write error log:', appendErr);
      if (callback) return callback(appendErr);
    }
    console.error(entry.trim());
    if (callback) callback(null);
  });
}

module.exports = {
  logInfo: logInfo,
  logError: logError
};
