<template>
  <div id="app" class="crm-app">
    <header class="navbar">
      <div class="brand">
        <span class="logo-icon">💼</span>
        <h1>FinTech CRM & Credit Facility Manager (Vue 2.6 Legacy)</h1>
      </div>
      <nav class="nav-tabs">
        <button
          :class="['tab-btn', currentTab === 'customers' ? 'active' : '']"
          @click="currentTab = 'customers'"
        >
          Credit Portfolios
        </button>
        <button
          :class="['tab-btn', currentTab === 'ledger' ? 'active' : '']"
          @click="currentTab = 'ledger'"
        >
          General Ledger & Invoicing
        </button>
      </nav>
      <div class="user-meta">
        <span class="user-badge">Risk Officer: Sarah Jenkins</span>
      </div>
    </header>

    <main class="content-body">
      <CustomerList v-if="currentTab === 'customers'" @view-ledger="switchToLedger" />
      <TransactionLedger v-if="currentTab === 'ledger'" />
    </main>
  </div>
</template>

<script>
import CustomerList from './views/CustomerList.vue';
import TransactionLedger from './views/TransactionLedger.vue';
import EventBus from './utils/eventBus';

export default {
  name: 'App',
  components: {
    CustomerList,
    TransactionLedger
  },
  data() {
    return {
      currentTab: 'customers'
    };
  },
  methods: {
    switchToLedger(customer) {
      this.currentTab = 'ledger';
    }
  },
  mounted() {
    EventBus.$on('credit-limit-updated', payload => {
      console.log('[Audit EventBus] Credit limit adjusted for customer ID:', payload.id);
    });
  },
  beforeDestroy() {
    EventBus.$off('credit-limit-updated');
  }
};
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; background-color: #f1f5f9; color: #1e293b; }
.crm-app { min-height: 100vh; display: flex; flex-direction: column; }
.navbar { background-color: #0f172a; color: white; padding: 0.8rem 2rem; display: flex; justify-content: space-between; align-items: center; }
.brand { display: flex; align-items: center; gap: 0.6rem; }
.brand h1 { font-size: 1.15rem; font-weight: 600; }
.logo-icon { font-size: 1.4rem; }
.nav-tabs { display: flex; gap: 0.5rem; }
.tab-btn { background: none; border: none; color: #94a3b8; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; font-size: 0.9rem; font-weight: 500; transition: all 0.2s; }
.tab-btn:hover { color: white; background: rgba(255,255,255,0.05); }
.tab-btn.active { color: white; background: #2563eb; }
.user-badge { font-size: 0.85rem; color: #cbd5e1; background: #1e293b; padding: 0.3rem 0.8rem; border-radius: 9999px; }
.content-body { padding: 2rem; flex: 1; max-width: 1300px; margin: 0 auto; width: 100%; }
</style>
