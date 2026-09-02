import Vue from 'vue';
import App from './App.vue';
import store from './store';

Vue.config.productionTip = false;

// Legacy Global EventBus Anti-Pattern
Vue.prototype.$eventBus = new Vue();

// Legacy Global Filters
Vue.filter('currency', function (value) {
  if (!value) return '$0.00';
  return '$' + parseFloat(value).toFixed(2);
});

Vue.filter('formatDate', function (value) {
  if (!value) return '';
  var date = new Date(value);
  return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
});

new Vue({
  store,
  render: function (h) {
    return h(App);
  }
}).$mount('#app');
