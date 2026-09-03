<template>
  <div class="transaction-ledger-view">
    <div class="summary-cards">
      <div class="card debit-card">
        <label>Total Debit Volume</label>
        <h2>{{ totalDebit | currency }}</h2>
      </div>
      <div class="card credit-card">
        <label>Total Credit Volume</label>
        <h2>{{ totalCredit | currency }}</h2>
      </div>
      <div class="card net-card">
        <label>Net Receivables</label>
        <h2>{{ netReceivables | currency }}</h2>
      </div>
    </div>

    <div class="control-panel">
      <div class="filters">
        <select v-model="selectedCategory" @change="onCategoryChange" class="filter-select">
          <option value="ALL">All Categories</option>
          <option value="CLOUD_INVOICE">Cloud Hosting Invoice</option>
          <option value="ANNUAL_LICENSE">Annual Software License</option>
          <option value="SECURITY_AUDIT">Security Audit Fee</option>
          <option value="BANK_WIRE">Bank Wire Receipt</option>
        </select>
        <select v-model="selectedType" @change="onTypeChange" class="filter-select">
          <option value="ALL">All Transaction Types</option>
          <option value="DEBIT">Debit (Invoice Issued)</option>
          <option value="CREDIT">Credit (Payment Received)</option>
        </select>
      </div>

      <div class="actions">
        <button @click="showPostModal = true" class="btn btn-primary">+ Post Journal Entry</button>
        <button @click="showExportModal = true" class="btn btn-secondary">Export Report</button>
      </div>
    </div>

    <table class="ledger-table">
      <thead>
        <tr>
          <th>Tx ID</th>
          <th>Date & Time</th>
          <th>Account / Customer</th>
          <th>Type</th>
          <th>Category</th>
          <th>Amount</th>
          <th>Status</th>
          <th>Memo / Audit Note</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="tx in transactions" :key="tx.id">
          <td><code>{{ tx.id }}</code></td>
          <td>{{ tx.date }}</td>
          <td><strong>{{ tx.customerName }}</strong></td>
          <td>
            <span :class="['type-pill', tx.type.toLowerCase()]">{{ tx.type }}</span>
          </td>
          <td>{{ tx.category }}</td>
          <td :class="['amount-col', tx.type.toLowerCase()]">
            {{ (tx.type === 'DEBIT' ? '+' : '-') + (tx.amount | currency) }}
          </td>
          <td>
            <span :class="['status-badge', tx.status.toLowerCase()]">{{ tx.status }}</span>
          </td>
          <td>{{ tx.note }}</td>
        </tr>
      </tbody>
    </table>

    <!-- Post Journal Modal -->
    <div v-if="showPostModal" class="modal-backdrop">
      <div class="modal-box">
        <h3>Post Financial Journal Entry</h3>
        <form @submit.prevent="submitJournalEntry">
          <div class="form-group">
            <label>Customer Account:</label>
            <select v-model="newEntry.customerId" class="form-control" required>
              <option v-for="c in customerList" :key="c.id" :value="c.id">{{ c.name }} (Bal: {{ c.currentBalance | currency }})</option>
            </select>
          </div>
          <div class="form-group">
            <label>Transaction Type:</label>
            <select v-model="newEntry.type" class="form-control">
              <option value="DEBIT">DEBIT (+ Increase Customer Balance)</option>
              <option value="CREDIT">CREDIT (- Deduct / Paydown Balance)</option>
            </select>
          </div>
          <div class="form-group">
            <label>Category:</label>
            <select v-model="newEntry.category" class="form-control">
              <option value="CLOUD_INVOICE">Cloud Hosting Invoice</option>
              <option value="ANNUAL_LICENSE">Annual Software License</option>
              <option value="SECURITY_AUDIT">Security Audit Fee</option>
              <option value="BANK_WIRE">Bank Wire Receipt</option>
            </select>
          </div>
          <div class="form-group">
            <label>Amount ($):</label>
            <input type="number" v-model.number="newEntry.amount" min="1" step="0.01" class="form-control" required />
          </div>
          <div class="form-group">
            <label>Audit Memo / Reference:</label>
            <input type="text" v-model="newEntry.note" placeholder="Wire ref / Invoice number" class="form-control" required />
          </div>
          <div class="modal-actions">
            <button type="button" @click="showPostModal = false" class="btn btn-secondary">Cancel</button>
            <button type="submit" class="btn btn-primary" v-debounce="{ handler: submitJournalEntry, delay: 800 }">Confirm Post</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Export Report Modal -->
    <ExportReportModal v-if="showExportModal" @close="showExportModal = false" />
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex';
import ExportReportModal from '../components/ExportReportModal.vue';
import { subtractMoney } from '../utils/moneyCalculator';

export default {
  name: 'TransactionLedger',
  components: {
    ExportReportModal
  },
  data() {
    return {
      selectedCategory: 'ALL',
      selectedType: 'ALL',
      showPostModal: false,
      showExportModal: false,
      newEntry: {
        customerId: 101,
        type: 'DEBIT',
        category: 'CLOUD_INVOICE',
        amount: 1000.0,
        note: '',
        status: 'SETTLED'
      }
    };
  },
  computed: {
    ...mapState('customer', {
      customerList: state => state.list
    }),
    ...mapGetters('ledger', {
      transactions: 'filteredTransactions',
      totalDebit: 'totalDebitVolume',
      totalCredit: 'totalCreditVolume'
    }),
    netReceivables() {
      return subtractMoney(this.totalDebit, this.totalCredit);
    }
  },
  methods: {
    onCategoryChange() {
      this.$store.commit('ledger/SET_CATEGORY_FILTER', this.selectedCategory);
    },
    onTypeChange() {
      this.$store.commit('ledger/SET_TYPE_FILTER', this.selectedType);
    },
    submitJournalEntry() {
      const cust = this.customerList.find(c => c.id === this.newEntry.customerId);
      const payload = {
        ...this.newEntry,
        customerName: cust ? cust.name : 'Unknown Customer'
      };
      this.$store.dispatch('ledger/postLedgerTransaction', payload).then(() => {
        this.showPostModal = false;
        this.newEntry.note = '';
      });
    }
  }
};
</script>

<style scoped>
.transaction-ledger-view { padding-top: 1rem; }
.summary-cards { display: flex; gap: 1.5rem; margin-bottom: 1.5rem; }
.card { background: white; padding: 1.5rem; border-radius: 8px; flex: 1; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.card label { font-size: 0.85rem; color: #64748b; font-weight: 600; text-transform: uppercase; }
.card h2 { font-size: 1.8rem; margin-top: 0.5rem; color: #0f172a; }
.debit-card h2 { color: #dc2626; }
.credit-card h2 { color: #16a34a; }
.net-card h2 { color: #2563eb; }
.control-panel { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.filters { display: flex; gap: 1rem; }
.filter-select { padding: 0.6rem 1rem; border: 1px solid #cbd5e1; border-radius: 6px; background: white; font-size: 0.9rem; }
.actions { display: flex; gap: 0.8rem; }
.btn { padding: 0.6rem 1.2rem; border-radius: 6px; border: none; cursor: pointer; font-weight: 500; font-size: 0.9rem; }
.btn-primary { background: #2563eb; color: white; }
.btn-secondary { background: #e2e8f0; color: #334155; }
.ledger-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.ledger-table th, .ledger-table td { padding: 0.85rem 1rem; text-align: left; border-bottom: 1px solid #f1f5f9; font-size: 0.9rem; }
.ledger-table th { background: #f8fafc; font-weight: 600; color: #475569; }
.type-pill { padding: 0.2rem 0.6rem; border-radius: 4px; font-size: 0.75rem; font-weight: bold; }
.type-pill.debit { background: #fee2e2; color: #991b1b; }
.type-pill.credit { background: #dcfce7; color: #166534; }
.amount-col.debit { color: #dc2626; font-weight: 600; }
.amount-col.credit { color: #16a34a; font-weight: 600; }
.status-badge { padding: 0.2rem 0.5rem; border-radius: 9999px; font-size: 0.75rem; }
.status-badge.settled { background: #e0f2fe; color: #0369a1; }
.status-badge.pending { background: #fef9c3; color: #854d0e; }
.modal-backdrop { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-box { background: white; padding: 2rem; border-radius: 8px; width: 480px; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.3rem; font-size: 0.85rem; font-weight: 600; color: #334155; }
.form-control { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 4px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.8rem; margin-top: 1.5rem; }
</style>
