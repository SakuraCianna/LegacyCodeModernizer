import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Legacy Axios Interceptor
apiClient.interceptors.response.use(
  function(response) {
    return response.data;
  },
  function(error) {
    console.error('[WMS API Error]', error);
    return Promise.reject(error);
  }
);

export default {
  getInventoryItems() {
    return apiClient.get('/inventory');
  },
  getLowStockAlerts(threshold) {
    return apiClient.get('/inventory/alerts/low-stock?threshold=' + (threshold || 10));
  },
  createItem(itemData) {
    return apiClient.post('/inventory', itemData);
  },
  adjustStock(id, delta) {
    return apiClient.patch('/inventory/' + id + '/stock?delta=' + delta);
  },
  deleteItem(id) {
    return apiClient.delete('/inventory/' + id);
  }
};
