import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    customers: [
      { id: 1, name: 'Acme Corp', contact: 'alice@acme.com', balance: 14500.0, status: 'ACTIVE', joinedAt: '2022-03-15' },
      { id: 2, name: 'Global Logistics', contact: 'bob@globallog.com', balance: 8200.5, status: 'PENDING', joinedAt: '2022-07-22' },
      { id: 3, name: 'Starlight Tech', contact: 'charlie@starlight.io', balance: 0.0, status: 'INACTIVE', joinedAt: '2023-01-10' }
    ],
    selectedCustomer: null
  },
  mutations: {
    SET_SELECTED_CUSTOMER(state, customer) {
      state.selectedCustomer = customer;
    },
    UPDATE_CUSTOMER_STATUS(state, payload) {
      const cust = state.customers.find(c => c.id === payload.id);
      if (cust) {
        cust.status = payload.status;
      }
    },
    ADD_CUSTOMER(state, newCust) {
      newCust.id = state.customers.length + 1;
      state.customers.push(newCust);
    }
  },
  actions: {
    updateStatus({ commit }, payload) {
      // Simulate async API call
      setTimeout(() => {
        commit('UPDATE_CUSTOMER_STATUS', payload);
      }, 300);
    }
  },
  getters: {
    activeCustomersCount(state) {
      return state.customers.filter(c => c.status === 'ACTIVE').length;
    },
    totalReceivables(state) {
      return state.customers.reduce((acc, c) => acc + c.balance, 0);
    }
  }
});
