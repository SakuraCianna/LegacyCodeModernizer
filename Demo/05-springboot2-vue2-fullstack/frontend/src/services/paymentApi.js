import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 8000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Response Interceptor
api.interceptors.response.use(
  response => response.data,
  error => {
    const errorMsg = error.response && error.response.data && error.response.data.message
      ? error.response.data.message
      : error.message;
    return Promise.reject(new Error(errorMsg));
  }
);

export default {
  createOrder(userId, title, amount) {
    return api.post('/payment/order/create', { userId, title, amount });
  },
  payOrder(payload) {
    return api.post('/payment/pay', payload);
  },
  getUserOrders(userId) {
    return api.get('/payment/user/' + userId + '/orders');
  },
  applyRefund(payload) {
    return api.post('/refund/apply', payload);
  },
  auditRefund(refundNo, approved, auditor) {
    return api.post('/refund/audit/' + refundNo, { approved, auditor });
  },
  getAllRefunds() {
    return api.get('/refund/list');
  },
  getWallet(userId) {
    return api.get('/wallet/' + userId);
  },
  depositWallet(userId, amount, remark) {
    return api.post('/wallet/' + userId + '/deposit', { amount, remark });
  },
  getAllAuditLogs() {
    return api.get('/wallet/audit-logs/all');
  }
};
