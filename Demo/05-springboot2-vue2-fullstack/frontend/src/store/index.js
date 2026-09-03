import Vue from 'vue';
import Vuex from 'vuex';
import api from '../services/paymentApi';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    currentUserId: 1001,
    currentUsername: 'Alex Developer',
    walletBalance: 2500.0,
    orders: [
      { id: 1, orderNo: 'ORD-20230901-8891', title: 'Enterprise Cloud Infrastructure (Annual)', totalAmount: 1200.0, refundedAmount: 0.0, status: 'SUCCESS', createdAt: '2023-09-01 10:20:00' },
      { id: 2, orderNo: 'ORD-20230902-7723', title: 'Dedicated Security HSM Hardware Module', totalAmount: 450.0, refundedAmount: 50.0, status: 'REFUND_PARTIAL', createdAt: '2023-09-02 14:15:30' },
      { id: 3, orderNo: 'ORD-20230903-1109', title: 'AI Code Review Engine API Quota', totalAmount: 89.9, refundedAmount: 0.0, status: 'CREATED', createdAt: '2023-09-03 09:00:00' }
    ],
    refundList: [],
    auditLogs: []
  },
  mutations: {
    SET_WALLET_BALANCE(state, balance) {
      state.walletBalance = balance;
    },
    SET_ORDERS(state, orders) {
      state.orders = orders;
    },
    ADD_ORDER(state, order) {
      state.orders.unshift(order);
    },
    UPDATE_ORDER_STATUS(state, { orderNo, status, refundedAmount }) {
      const ord = state.orders.find(o => o.orderNo === orderNo);
      if (ord) {
        ord.status = status;
        if (refundedAmount !== undefined) ord.refundedAmount = refundedAmount;
      }
    },
    SET_REFUNDS(state, list) {
      state.refundList = list;
    },
    SET_AUDIT_LOGS(state, logs) {
      state.auditLogs = logs;
    }
  },
  actions: {
    refreshWallet({ commit, state }) {
      api.getWallet(state.currentUserId).then(wallet => {
        commit('SET_WALLET_BALANCE', Number(wallet.balance));
      }).catch(() => {});
    },
    refreshOrders({ commit, state }) {
      api.getUserOrders(state.currentUserId).then(orders => {
        if (orders && orders.length > 0) commit('SET_ORDERS', orders);
      }).catch(() => {});
    }
  }
});
