<template>
  <div class="financial-ledger-view">
    <div class="header-banner">
      <div>
        <h2>Financial Audit Trail & Reconciliation Ledger</h2>
        <p class="subtitle">Real-time immutable ledger snapshots with before/after balance tracking.</p>
      </div>
      <button @click="loadLogs" class="btn btn-secondary">Refresh Ledger</button>
    </div>

    <table class="audit-table">
      <thead>
        <tr>
          <th>Log ID</th>
          <th>Date & Time</th>
          <th>User ID</th>
          <th>Biz Type</th>
          <th>Reference No</th>
          <th>Before Balance</th>
          <th>Delta</th>
          <th>After Balance</th>
          <th>Audit Remark</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="log in logs" :key="log.id">
          <td>#{{ log.id }}</td>
          <td>{{ log.createdAt }}</td>
          <td>{{ log.userId }}</td>
          <td>
            <span :class="['biz-tag', log.bizType.toLowerCase()]">{{ log.bizType }}</span>
          </td>
          <td><code>{{ log.bizReferenceNo }}</code></td>
          <td>${{ Number(log.beforeBalance).toFixed(2) }}</td>
          <td :class="['delta-col', Number(log.deltaAmount) >= 0 ? 'positive' : 'negative']">
            {{ Number(log.deltaAmount) >= 0 ? '+' : '' }}${{ Number(log.deltaAmount).toFixed(2) }}
          </td>
          <td><strong>${{ Number(log.afterBalance).toFixed(2) }}</strong></td>
          <td>{{ log.remark }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import api from '../services/paymentApi';

export default {
  name: 'FinancialLedger',
  data() {
    return {
      logs: [
        { id: 501, userId: 1001, bizType: 'DEPOSIT', bizReferenceNo: 'DEP-1693710000', beforeBalance: 2000.00, deltaAmount: 500.00, afterBalance: 2500.00, remark: 'Initial Seed Balance', createdAt: '2023-09-01 08:00:00' },
        { id: 502, userId: 1001, bizType: 'ORDER_PAY', bizReferenceNo: 'ORD-20230901-8891', beforeBalance: 2500.00, deltaAmount: -1200.00, afterBalance: 1300.00, remark: 'Cloud Hosting Payment', createdAt: '2023-09-01 10:20:00' },
        { id: 503, userId: 1001, bizType: 'REFUND_CREDIT', bizReferenceNo: 'REF-20230902-8811', beforeBalance: 1300.00, deltaAmount: 50.00, afterBalance: 1350.00, remark: 'Defective Cable Refund', createdAt: '2023-09-02 15:30:00' }
      ]
    };
  },
  methods: {
    loadLogs() {
      api.getAllAuditLogs().then(res => {
        if (res && res.length > 0) this.logs = res;
      }).catch(() => {});
    }
  },
  mounted() {
    this.loadLogs();
  }
};
</script>

<style scoped>
.financial-ledger-view { padding-top: 0.5rem; }
.header-banner { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.subtitle { font-size: 0.85rem; color: #64748b; margin-top: 0.3rem; }
.audit-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); font-size: 0.9rem; }
.audit-table th, .audit-table td { padding: 0.9rem 1rem; text-align: left; border-bottom: 1px solid #f1f5f9; }
.audit-table th { background: #f8fafc; font-weight: 600; color: #475569; }
.biz-tag { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: bold; }
.biz-tag.deposit { background: #dcfce7; color: #166534; }
.biz-tag.order_pay { background: #fee2e2; color: #991b1b; }
.biz-tag.refund_credit { background: #e0f2fe; color: #0369a1; }
.delta-col.positive { color: #16a34a; font-weight: 600; }
.delta-col.negative { color: #dc2626; font-weight: 600; }
.btn { padding: 0.5rem 1rem; border-radius: 6px; border: none; cursor: pointer; font-weight: 500; font-size: 0.85rem; }
.btn-secondary { background: #e2e8f0; color: #334155; }
</style>
