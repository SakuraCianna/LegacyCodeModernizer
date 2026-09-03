import { addMoney, subtractMoney } from '../../utils/moneyCalculator';

const state = {
  list: [
    { id: 101, name: 'Apex Capital Partners', email: 'billing@apexcap.com', creditLimit: 50000.0, currentBalance: 32000.0, riskRating: 'A', status: 'ACTIVE', tier: 'ENTERPRISE' },
    { id: 102, name: 'BlueHorizon Freight', email: 'finance@bluehorizon.com', creditLimit: 20000.0, currentBalance: 19500.0, riskRating: 'C', status: 'WARNING', tier: 'PRO' },
    { id: 103, name: 'CyberShield InfoSec', email: 'accounts@cybershield.io', creditLimit: 10000.0, currentBalance: 1200.0, riskRating: 'A', status: 'ACTIVE', tier: 'GROWTH' },
    { id: 104, name: 'OmniVenture Media', email: 'ap@omniventure.net', creditLimit: 15000.0, currentBalance: 15000.0, riskRating: 'D', status: 'FROZEN', tier: 'STANDARD' }
  ],
  selectedCustomer: null,
  loading: false
};

const mutations = {
  SET_CUSTOMERS(state, customers) {
    state.list = customers;
  },
  SET_SELECTED(state, customer) {
    state.selectedCustomer = customer;
  },
  SET_LOADING(state, flag) {
    state.loading = flag;
  },
  ADD_CUSTOMER(state, newCust) {
    newCust.id = state.list.length > 0 ? Math.max(...state.list.map(c => c.id)) + 1 : 101;
    state.list.unshift(newCust);
  },
  UPDATE_CREDIT_LIMIT(state, { id, creditLimit }) {
    const cust = state.list.find(c => c.id === id);
    if (cust) {
      cust.creditLimit = creditLimit;
    }
  },
  ADJUST_BALANCE(state, { id, delta }) {
    const cust = state.list.find(c => c.id === id);
    if (cust) {
      cust.currentBalance = addMoney(cust.currentBalance, delta);
      // Dynamic risk badge adjustment
      const ratio = cust.currentBalance / cust.creditLimit;
      if (ratio > 0.95) {
        cust.status = 'WARNING';
        cust.riskRating = 'C';
      } else if (ratio > 1.0) {
        cust.status = 'FROZEN';
        cust.riskRating = 'D';
      } else {
        cust.status = 'ACTIVE';
        cust.riskRating = 'A';
      }
    }
  }
};

const actions = {
  fetchCustomerList({ commit }) {
    commit('SET_LOADING', true);
    setTimeout(() => {
      commit('SET_LOADING', false);
    }, 200);
  },
  saveCustomer({ commit }, customerData) {
    return new Promise((resolve) => {
      setTimeout(() => {
        commit('ADD_CUSTOMER', customerData);
        resolve(customerData);
      }, 300);
    });
  },
  modifyCreditLimit({ commit }, payload) {
    commit('UPDATE_CREDIT_LIMIT', payload);
  }
};

const getters = {
  totalCreditExposure(state) {
    return state.list.reduce((acc, c) => addMoney(acc, c.currentBalance), 0);
  },
  totalCreditLimit(state) {
    return state.list.reduce((acc, c) => addMoney(acc, c.creditLimit), 0);
  },
  warningAccountsCount(state) {
    return state.list.filter(c => c.status === 'WARNING' || c.status === 'FROZEN').length;
  },
  customerById: (state) => (id) => {
    return state.list.find(c => c.id === id);
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
};
