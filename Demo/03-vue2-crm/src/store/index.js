import Vue from 'vue';
import Vuex from 'vuex';
import customer from './modules/customer';
import ledger from './modules/ledger';

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    customer,
    ledger
  }
});
