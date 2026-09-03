<template>
  <div class="refund-manager-view">
    <div class="header-action">
      <h2>Order Refund Processing & Dispute Settlement</h2>
      <button @click="showApplyModal = true" class="btn btn-warning">+ Initiate Refund Application</button>
    </div>

    <table class="refund-table">
      <thead>
        <tr>
          <th>Refund No</th>
          <th>Order No</th>
          <th>User ID</th>
          <th>Refund Amount</th>
          <th>Reason</th>
          <th>Auditor</th>
          <th>Status</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in refunds" :key="r.id">
          <td><code>{{ r.refundNo }}</code></td>
          <td>{{ r.orderNo }}</td>
          <td>{{ r.userId }}</td>
          <td><strong class="text-danger">${{ Number(r.refundAmount).toFixed(2) }}</strong></td>
          <td>{{ r.reason }}</td>
          <td>{{ r.auditBy }}</td>
          <td>
            <span :class="['status-badge', r.status.toLowerCase()]">{{ r.status }}</span>
          </td>
          <td>
            <div v-if="r.status === 'PENDING'" class="btn-group">
              <button @click="handleAudit(r.refundNo, true)" class="btn btn-sm btn-approve">Approve</button>
              <button @click="handleAudit(r.refundNo, false)" class="btn btn-sm btn-reject">Reject</button>
            </div>
            <span v-else class="text-muted">Processed</span>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Apply Refund Modal -->
    <div v-if="showApplyModal" class="modal-overlay">
      <div class="modal-box">
        <h3>Initiate Refund Request</h3>
        <form @submit.prevent="submitRefund">
          <div class="form-group">
            <label>Select Paid Order:</label>
            <select v-model="selectedOrderNo" class="form-control" required>
              <option v-for="o in paidOrders" :key="o.id" :value="o.orderNo">
                {{ o.orderNo }} - {{ o.title }} (Max Refund: ${{ (o.totalAmount - o.refundedAmount).toFixed(2) }})
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Refund Amount ($):</label>
            <input type="number" v-model.number="refundAmount" min="0.01" step="0.01" class="form-control" required />
          </div>
          <div class="form-group">
            <label>Reason for Dispute / Return:</label>
            <input type="text" v-model="refundReason" placeholder="Service SLA breach / Product return" class="form-control" required />
          </div>
          <div class="modal-actions">
            <button type="button" @click="showApplyModal = false" class="btn btn-secondary">Cancel</button>
            <button type="submit" class="btn btn-warning">Submit for Audit</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import api from '../services/paymentApi';

export default {
  name: 'RefundManager',
  data() {
    return {
      refunds: [
        { id: 1, refundNo: 'REF-20230902-8811', orderNo: 'ORD-20230902-7723', userId: 1001, refundAmount: 50.00, reason: 'Returned defective cable', auditBy: 'AuditDesk01', status: 'SUCCESS' },
        { id: 2, refundNo: 'REF-20230903-9934', orderNo: 'ORD-20230901-8891', userId: 1001, refundAmount: 200.00, reason: 'Service SLA downtime compensation', auditBy: 'AuditDesk01', status: 'PENDING' }
      ],
      showApplyModal: false,
      selectedOrderNo: '',
      refundAmount: 50.00,
      refundReason: ''
    };
  },
  computed: {
    ...mapState(['currentUserId', 'orders']),
    paidOrders() {
      return this.orders.filter(o => o.status === 'SUCCESS' || o.status === 'REFUND_PARTIAL');
    }
  },
  methods: {
    loadRefunds() {
      api.getAllRefunds().then(res => {
        if (res && res.length > 0) this.refunds = res;
      }).catch(() => {});
    },
    submitRefund() {
      api.applyRefund({
        orderNo: this.selectedOrderNo,
        userId: this.currentUserId,
        refundAmount: this.refundAmount,
        reason: this.refundReason,
        auditBy: 'ComplianceAuditor'
      }).then(res => {
        this.refunds.unshift(res);
        this.showApplyModal = false;
        alert('Refund application submitted for audit!');
      }).catch(err => {
        alert('Refund application failed: ' + err.message);
      });
    },
    handleAudit(refundNo, approved) {
      api.auditRefund(refundNo, approved, 'ChiefRiskOfficer').then(res => {
        const item = this.refunds.find(r => r.refundNo === refundNo);
        if (item) item.status = res.status;
        this.$store.dispatch('refreshWallet');
        this.$store.dispatch('refreshOrders');
        alert('Refund audit finished: ' + res.status);
      }).catch(err => {
        alert('Audit failed: ' + err.message);
      });
    }
  },
  mounted() {
    this.loadRefunds();
  }
};
</script>

<style scoped>
.refund-manager-view { padding-top: 0.5rem; }
.header-action { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.refund-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.refund-table th, .refund-table td { padding: 1rem; text-align: left; border-bottom: 1px solid #f1f5f9; font-size: 0.9rem; }
.refund-table th { background: #f8fafc; font-weight: 600; color: #475569; }
.text-danger { color: #dc2626; }
.status-badge { padding: 0.25rem 0.6rem; border-radius: 9999px; font-size: 0.75rem; font-weight: bold; }
.status-badge.pending { background: #fef9c3; color: #854d0e; }
.status-badge.success { background: #dcfce7; color: #166534; }
.status-badge.rejected { background: #fee2e2; color: #991b1b; }
.btn-group { display: flex; gap: 0.4rem; }
.btn-approve { background: #16a34a; color: white; border: none; padding: 0.3rem 0.6rem; border-radius: 4px; cursor: pointer; }
.btn-reject { background: #ef4444; color: white; border: none; padding: 0.3rem 0.6rem; border-radius: 4px; cursor: pointer; }
.btn { padding: 0.6rem 1.2rem; border-radius: 6px; border: none; cursor: pointer; font-weight: 500; }
.btn-warning { background: #d97706; color: white; }
.btn-secondary { background: #e2e8f0; color: #334155; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-box { background: white; padding: 2rem; border-radius: 8px; width: 480px; }
.form-group { margin-bottom: 1.2rem; }
.form-group label { display: block; margin-bottom: 0.4rem; font-size: 0.85rem; font-weight: 600; color: #334155; }
.form-control { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 4px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.8rem; margin-top: 1.5rem; }
</style>
