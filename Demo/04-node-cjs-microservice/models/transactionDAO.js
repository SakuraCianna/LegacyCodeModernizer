// File-backed Transaction DAO using JSON persistence and callback pyramids
const fs = require('fs');
const path = require('path');
const config = require('../config/default');

const DATA_FILE = path.resolve(__dirname, '..', config.storage.transactionsPath);

function ensureDataFile(callback) {
  const dir = path.dirname(DATA_FILE);
  fs.mkdir(dir, { recursive: true }, function(err) {
    if (err) return callback(err);
    fs.access(DATA_FILE, fs.constants.F_OK, function(accessErr) {
      if (accessErr) {
        fs.writeFile(DATA_FILE, '[]', 'utf8', callback);
      } else {
        callback(null);
      }
    });
  });
}

function findAll(callback) {
  ensureDataFile(function(initErr) {
    if (initErr) return callback(initErr);
    fs.readFile(DATA_FILE, 'utf8', function(readErr, data) {
      if (readErr) return callback(readErr);
      try {
        const list = JSON.parse(data);
        callback(null, list);
      } catch (parseErr) {
        callback(null, []);
      }
    });
  });
}

function findById(id, callback) {
  findAll(function(err, list) {
    if (err) return callback(err);
    const item = list.find(function(t) { return t.id === id; });
    callback(null, item || null);
  });
}

function save(transaction, callback) {
  findAll(function(err, list) {
    if (err) return callback(err);
    list.push(transaction);
    fs.writeFile(DATA_FILE, JSON.stringify(list, null, 2), 'utf8', function(writeErr) {
      if (writeErr) return callback(writeErr);
      callback(null, transaction);
    });
  });
}

function updateStatus(id, newStatus, callback) {
  findAll(function(err, list) {
    if (err) return callback(err);
    const item = list.find(function(t) { return t.id === id; });
    if (!item) {
      return callback(new Error('TRANSACTION_NOT_FOUND'));
    }
    item.status = newStatus;
    item.updatedAt = new Date().toISOString();
    fs.writeFile(DATA_FILE, JSON.stringify(list, null, 2), 'utf8', function(writeErr) {
      if (writeErr) return callback(writeErr);
      callback(null, item);
    });
  });
}

module.exports = {
  findAll: findAll,
  findById: findById,
  save: save,
  updateStatus: updateStatus
};
