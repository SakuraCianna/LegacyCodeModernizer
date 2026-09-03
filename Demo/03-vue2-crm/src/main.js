import Vue from 'vue';
import App from './App.vue';
import store from './store';
import debounce from './directives/debounce';
import { formatCurrency } from './utils/moneyCalculator';

Vue.config.productionTip = false;

// Register custom directives
Vue.directive('debounce', debounce);

// Register legacy global filters
Vue.filter('currency', function(value, symbol) {
  return formatCurrency(value, symbol);
});

Vue.filter('dateSimple', function(value) {
  if (!value) return '';
  return String(value).split(' ')[0];
});

new Vue({
  store,
  render: h => h(App)
}).$mount('#app');
