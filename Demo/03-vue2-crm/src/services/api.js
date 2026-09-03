import axios from 'axios';

const client = axios.create({
  baseURL: '/api/v1',
  timeout: 6000,
  headers: {
    'Content-Type': 'application/json',
    'X-Client-Version': 'Vue2-Legacy-2021'
  }
});

client.interceptors.request.use(
  config => {
    // Legacy token injection from localStorage
    const token = localStorage.getItem('crm_auth_token');
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }
    return config;
  },
  error => Promise.reject(error)
);

client.interceptors.response.use(
  response => response.data,
  error => {
    const status = error.response ? error.response.status : null;
    if (status === 401) {
      console.warn('[CRM API] Session expired, redirecting to login');
    }
    return Promise.reject(error);
  }
);

export default {
  fetchCustomers() {
    return client.get('/customers');
  },
  createCustomer(payload) {
    return client.post('/customers', payload);
  },
  updateCreditLimit(id, limit) {
    return client.put('/customers/' + id + '/credit-limit', { creditLimit: limit });
  },
  fetchLedgerEntries(params) {
    return client.get('/ledger', { params });
  },
  recordLedgerEntry(entry) {
    return client.post('/ledger', entry);
  },
  exportAuditReport(format) {
    return client.get('/reports/export', { params: { format } });
  }
};
