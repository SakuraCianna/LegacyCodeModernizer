<template>
  <div class="customer-list-view">
    <div class="kpi-panel">
      <div class="kpi-card">
        <label>Total Credit Approved</label>
        <p class="kpi-num">{{ totalCreditLimit | currency }}</p>
      </div>
      <div class="kpi-card">
        <label>Current Active Exposure</label>
        <p class="kpi-num">{{ totalExposure | currency }}</p>
      </div>
      <div class="kpi-card warning-card">
        <label>High Risk / Frozen Accounts</label>
        <p class="kpi-num warning-text">{{ warningCount }} Accounts</p>
      </div>
    </div>

    <div class="filter-bar">
      <input type="text" v-model="searchQuery" placeholder="Search enterprise clients by name or billing email..." class="search-input" />
      <select v-model="riskFilter" class="select-box">
        <option value="ALL">All Risk Ratings</option>
        <option value="A">Grade A (Prime)</option>
        <option value="B">Grade B (Standard)</option>
        <option value="C">Grade C (Warning)</option>
        <option value="D">Grade D (Frozen)</option>
      </select>
      <button @click="showAddModal = true" class="btn btn-primary">+ Open Credit Facility</button>
    </div>

    <!-- Credit Score Cards Grid -->
    <div class="cards-grid">
      <CreditScoreCard
        v-for="customer in filteredCustomers"
        :key="customer.id"
        :customer="customer"
        @update-limit="onUpdateLimit"
        @select-customer="onSelectCustomer"
      />
    </div>

    <CustomerModal
      v-if="showAddModal"
      @close="showAddModal = false"
      @save="onCustomerSaved"
    />
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
import CreditScoreCard from '../components/CreditScoreCard.vue';
import CustomerModal from '../components/CustomerModal.vue';
import EventBus from '../utils/eventBus';

export default {
  name: 'CustomerList',
  components: {
    CreditScoreCard,
    CustomerModal
  },
  data() {
    return {
      searchQuery: '',
      riskFilter: 'ALL',
      showAddModal: false
    };
  },
  computed: {
    ...mapState('customer', {
      customers: state => state.list
    }),
    ...mapGetters('customer', {
      totalExposure: 'totalCreditExposure',
      totalCreditLimit: 'totalCreditLimit',
      warningCount: 'warningAccountsCount'
    }),
    filteredCustomers() {
      const q = this.searchQuery.toLowerCase();
      return this.customers.filter(c => {
        const matchesQuery = c.name.toLowerCase().includes(q) || c.email.toLowerCase().includes(q);
        const matchesRisk = this.riskFilter === 'ALL' || c.riskRating === this.riskFilter;
        return matchesQuery && matchesRisk;
      });
    }
  },
  methods: {
    onUpdateLimit(payload) {
      this.$store.dispatch('customer/modifyCreditLimit', payload);
      EventBus.$emit('credit-limit-updated', payload);
    },
    onSelectCustomer(customer) {
      this.$emit('view-ledger', customer);
    },
    onCustomerSaved(newCustomer) {
      this.$store.dispatch('customer/saveCustomer', newCustomer).then(() => {
        this.showAddModal = false;
      });
    }
  }
};
</script>

<style scoped>
.kpi-panel { display: flex; gap: 1.5rem; margin-bottom: 1.5rem; }
.kpi-card { background: white; padding: 1.2rem 1.5rem; border-radius: 8px; flex: 1; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.kpi-card label { font-size: 0.8rem; font-weight: 600; color: #64748b; text-transform: uppercase; }
.kpi-num { font-size: 1.8rem; font-weight: bold; color: #0f172a; margin-top: 0.4rem; }
.warning-card { border-top: 3px solid #ef4444; }
.warning-text { color: #dc2626; }
.filter-bar { display: flex; gap: 1rem; margin-bottom: 1.5rem; }
.search-input { flex: 1; padding: 0.75rem 1rem; border: 1px solid #cbd5e1; border-radius: 6px; background: white; font-size: 0.95rem; }
.select-box { padding: 0.75rem 1rem; border: 1px solid #cbd5e1; border-radius: 6px; background: white; }
.btn-primary { background: #2563eb; color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 6px; font-weight: 500; cursor: pointer; }
.cards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 1.2rem; }
</style>
