<template>
  <div class="customer-list-view">
    <div class="kpi-panel">
      <div class="kpi-card">
        <h3>Active Accounts</h3>
        <p class="kpi-num">{{ activeCount }}</p>
      </div>
      <div class="kpi-card">
        <h3>Total Receivables</h3>
        <p class="kpi-num">{{ totalReceivables | currency }}</p>
      </div>
    </div>

    <div class="filter-bar">
      <input type="text" v-model="searchQuery" placeholder="Search by name or email..." class="search-input" />
      <select v-model="statusFilter" class="status-select">
        <option value="ALL">All Statuses</option>
        <option value="ACTIVE">Active</option>
        <option value="PENDING">Pending</option>
        <option value="INACTIVE">Inactive</option>
      </select>
      <button @click="showAddModal = true" class="btn btn-primary">+ New Customer</button>
    </div>

    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Company Name</th>
          <th>Contact Email</th>
          <th>Balance</th>
          <th>Joined Date</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="customer in filteredCustomers" :key="customer.id">
          <td>{{ customer.id }}</td>
          <td><strong>{{ customer.name }}</strong></td>
          <td>{{ customer.contact }}</td>
          <td>{{ customer.balance | currency }}</td>
          <td>{{ customer.joinedAt | formatDate }}</td>
          <td>
            <span :class="['status-badge', customer.status.toLowerCase()]">{{ customer.status }}</span>
          </td>
          <td>
            <button @click="toggleStatus(customer)" class="btn btn-sm">Toggle Status</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Legacy Child Component Interaction via $emit and Props -->
    <CustomerModal
      v-if="showAddModal"
      @close="showAddModal = false"
      @save="onCustomerSaved"
    />
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
import CustomerModal from '../components/CustomerModal.vue';

export default {
  name: 'CustomerList',
  components: {
    CustomerModal
  },
  data() {
    return {
      searchQuery: '',
      statusFilter: 'ALL',
      showAddModal: false
    };
  },
  computed: {
    ...mapState({
      customers: state => state.customers
    }),
    ...mapGetters({
      activeCount: 'activeCustomersCount',
      totalReceivables: 'totalReceivables'
    }),
    filteredCustomers() {
      const q = this.searchQuery.toLowerCase();
      return this.customers.filter(c => {
        const matchesQuery = c.name.toLowerCase().includes(q) || c.contact.toLowerCase().includes(q);
        const matchesStatus = this.statusFilter === 'ALL' || c.status === this.statusFilter;
        return matchesQuery && matchesStatus;
      });
    }
  },
  methods: {
    toggleStatus(customer) {
      const nextStatus = customer.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
      this.$store.dispatch('updateStatus', { id: customer.id, status: nextStatus });
      
      // Legacy EventBus Broadcast
      this.$eventBus.$emit('customer-status-changed', { id: customer.id, status: nextStatus });
    },
    onCustomerSaved(newCustomer) {
      this.$store.commit('ADD_CUSTOMER', newCustomer);
      this.showAddModal = false;
    }
  },
  mounted() {
    this.$eventBus.$on('customer-status-changed', payload => {
      console.log('[EventBus] Status updated for customer:', payload.id);
    });
  },
  beforeDestroy() {
    this.$eventBus.$off('customer-status-changed');
  }
};
</script>

<style scoped>
.kpi-panel { display: flex; gap: 1.5rem; margin-bottom: 2rem; }
.kpi-card { background: white; padding: 1.5rem; border-radius: 8px; flex: 1; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.kpi-num { font-size: 2rem; font-weight: bold; color: #2563eb; margin-top: 0.5rem; }
.filter-bar { display: flex; gap: 1rem; margin-bottom: 1.5rem; }
.search-input { flex: 1; padding: 0.75rem 1rem; border: 1px solid #cbd5e1; border-radius: 6px; }
.status-select { padding: 0.75rem; border: 1px solid #cbd5e1; border-radius: 6px; }
.btn { padding: 0.75rem 1.5rem; border: none; border-radius: 6px; cursor: pointer; font-weight: 500; }
.btn-primary { background: #2563eb; color: white; }
.btn-sm { padding: 0.4rem 0.8rem; background: #e2e8f0; }
.data-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.data-table th, .data-table td { padding: 1rem; text-align: left; border-bottom: 1px solid #f1f5f9; }
.data-table th { background: #f8fafc; font-weight: 600; color: #64748b; }
.status-badge { padding: 0.25rem 0.6rem; border-radius: 9999px; font-size: 0.8rem; font-weight: bold; }
.status-badge.active { background: #dcfce7; color: #166534; }
.status-badge.pending { background: #fef9c3; color: #854d0e; }
.status-badge.inactive { background: #fee2e2; color: #991b1b; }
</style>
