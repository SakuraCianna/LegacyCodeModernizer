<template>
  <div id="app" class="pay-app">
    <header class="top-nav">
      <div class="brand">
        <span class="logo">💎</span>
        <h1>Enterprise Payment & Refund Core Gateway (Spring Boot 2.7 + Vue 2.7)</h1>
      </div>
      <nav class="nav-links">
        <button :class="['tab-btn', currentView === 'cashier' ? 'active' : '']" @click="currentView = 'cashier'">
          Cashier Desk & Orders
        </button>
        <button :class="['tab-btn', currentView === 'refund' ? 'active' : '']" @click="currentView = 'refund'">
          Refund & Dispute Desk
        </button>
        <button :class="['tab-btn', currentView === 'ledger' ? 'active' : '']" @click="currentView = 'ledger'">
          Financial Audit Ledger
        </button>
      </nav>
      <div class="user-chip">
        <span>👤 {{ currentUsername }}</span>
        <span class="balance-pill">${{ walletBalance.toFixed(2) }}</span>
      </div>
    </header>

    <main class="main-container">
      <PaymentCashier v-if="currentView === 'cashier'" />
      <RefundManager v-if="currentView === 'refund'" />
      <FinancialLedger v-if="currentView === 'ledger'" />
    </main>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import PaymentCashier from './views/PaymentCashier.vue';
import RefundManager from './views/RefundManager.vue';
import FinancialLedger from './views/FinancialLedger.vue';

export default {
  name: 'App',
  components: {
    PaymentCashier,
    RefundManager,
    FinancialLedger
  },
  data() {
    return {
      currentView: 'cashier'
    };
  },
  computed: {
    ...mapState(['currentUsername', 'walletBalance'])
  }
};
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background-color: #f8fafc; color: #1e293b; }
.pay-app { min-height: 100vh; display: flex; flex-direction: column; }
.top-nav { background: #0f172a; color: white; padding: 0.8rem 2rem; display: flex; justify-content: space-between; align-items: center; }
.brand { display: flex; align-items: center; gap: 0.6rem; }
.brand h1 { font-size: 1.1rem; font-weight: 600; }
.logo { font-size: 1.3rem; }
.nav-links { display: flex; gap: 0.5rem; }
.tab-btn { background: none; border: none; color: #94a3b8; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; font-size: 0.9rem; font-weight: 500; transition: all 0.2s; }
.tab-btn:hover { color: white; background: rgba(255,255,255,0.05); }
.tab-btn.active { color: white; background: #2563eb; }
.user-chip { display: flex; align-items: center; gap: 0.6rem; background: #1e293b; padding: 0.3rem 0.8rem; border-radius: 9999px; font-size: 0.85rem; }
.balance-pill { background: #16a34a; color: white; padding: 0.15rem 0.5rem; border-radius: 9999px; font-weight: bold; font-size: 0.8rem; }
.main-container { padding: 2rem; flex: 1; max-width: 1300px; margin: 0 auto; width: 100%; }
</style>
