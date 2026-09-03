import { addMoney } from '../../utils/moneyCalculator';

const state = {
  transactions: [
    { id: 'TX-9001', customerId: 101, customerName: 'Apex Capital Partners', type: 'DEBIT', amount: 5000.0, category: 'CLOUD_INVOICE', note: 'Q3 AWS Cluster Hosting', date: '2023-09-01 14:22:00', status: 'SETTLED' },
    { id: 'TX-9002', customerId: 102, customerName: 'BlueHorizon Freight', type: 'DEBIT', amount: 12000.0, category: 'ANNUAL_LICENSE', note: 'Enterprise ERP Seat Expansion', date: '2023-09-02 09:10:15', status: 'PENDING' },
    { id: 'TX-9003', customerId: 101, customerName: 'Apex Capital Partners', type: 'CREDIT', amount: 3000.0, category: 'BANK_WIRE', note: 'Wire Transfer Reference #88112', date: '2023-09-03 16:45:00', status: 'SETTLED' },
    { id: 'TX-9004', customerId: 103, customerName: 'CyberShield InfoSec', type: 'DEBIT', amount: 1200.0, category: 'SECURITY_AUDIT', note: 'SOC2 Penetration Testing Service', date: '2023-09-04 11:30:00', status: 'SETTLED' }
  ],
  filterCategory: 'ALL',
  filterType: 'ALL'
};

const mutations = {
  ADD_TRANSACTION(state, tx) {
    state.transactions.unshift(tx);
  },
  SET_CATEGORY_FILTER(state, category) {
    state.filterCategory = category;
  },
  SET_TYPE_FILTER(state, type) {
    state.filterType = type;
  }
};

const actions = {
  postLedgerTransaction({ commit, dispatch }, txData) {
    return new Promise((resolve) => {
      txData.id = 'TX-' + Math.floor(1000 + Math.random() * 9000);
      txData.date = new Date().toISOString().replace('T', ' ').substring(0, 19);
      commit('ADD_TRANSACTION', txData);

      // Adjust customer balance accordingly
      const delta = txData.type === 'DEBIT' ? txData.amount : -txData.amount;
      dispatch('customer/modifyCreditLimit', { id: txData.customerId, delta }, { root: true });
      resolve(txData);
    });
  }
};

const getters = {
  filteredTransactions(state) {
    return state.transactions.filter(t => {
      const matchCat = state.filterCategory === 'ALL' || t.category === state.filterCategory;
      const matchType = state.filterType === 'ALL' || t.type === state.filterType;
      return matchCat && matchType;
    });
  },
  totalDebitVolume(state) {
    return state.transactions
      .filter(t => t.type === 'DEBIT')
      .reduce((acc, t) => addMoney(acc, t.amount), 0);
  },
  totalCreditVolume(state) {
    return state.transactions
      .filter(t => t.type === 'CREDIT')
      .reduce((acc, t) => addMoney(acc, t.amount), 0);
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
};
