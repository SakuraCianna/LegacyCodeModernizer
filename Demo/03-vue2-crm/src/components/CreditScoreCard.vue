<template>
  <div class="credit-score-card" :class="riskTierClass">
    <div class="card-header">
      <h4>{{ customer.name }}</h4>
      <span class="badge">{{ customer.riskRating }} RATING</span>
    </div>
    <div class="card-metrics">
      <div class="metric">
        <label>Credit Limit</label>
        <strong>{{ customer.creditLimit | currency }}</strong>
      </div>
      <div class="metric">
        <label>Utilized</label>
        <strong>{{ customer.currentBalance | currency }}</strong>
      </div>
      <div class="metric">
        <label>Utilization Rate</label>
        <strong>{{ utilizationPercent }}%</strong>
      </div>
    </div>
    <div class="progress-bar-bg">
      <div class="progress-bar-fill" :style="{ width: utilizationBarWidth + '%' }"></div>
    </div>
    <div class="card-footer">
      <button @click="onAdjustLimit" class="btn-text">Edit Limit</button>
      <button @click="onInspectLedger" class="btn-text">View Ledger</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CreditScoreCard',
  props: {
    customer: {
      type: Object,
      required: true
    }
  },
  computed: {
    utilizationPercent() {
      if (!this.customer.creditLimit || this.customer.creditLimit <= 0) return 0;
      return Math.min(100, Math.round((this.customer.currentBalance / this.customer.creditLimit) * 100));
    },
    utilizationBarWidth() {
      return Math.min(100, this.utilizationPercent);
    },
    riskTierClass() {
      if (this.utilizationPercent > 90) return 'tier-critical';
      if (this.utilizationPercent > 70) return 'tier-warning';
      return 'tier-safe';
    }
  },
  methods: {
    onAdjustLimit() {
      const newLimitStr = prompt('Enter new credit limit for ' + this.customer.name, this.customer.creditLimit);
      if (newLimitStr !== null) {
        const newLimit = parseFloat(newLimitStr);
        if (!isNaN(newLimit) && newLimit >= 0) {
          this.$emit('update-limit', { id: this.customer.id, creditLimit: newLimit });
        }
      }
    },
    onInspectLedger() {
      this.$emit('select-customer', this.customer);
    }
  }
};
</script>

<style scoped>
.credit-score-card { background: white; border-radius: 8px; padding: 1.2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border-left: 4px solid #cbd5e1; }
.credit-score-card.tier-safe { border-left-color: #22c55e; }
.credit-score-card.tier-warning { border-left-color: #eab308; }
.credit-score-card.tier-critical { border-left-color: #ef4444; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.card-header h4 { font-size: 1.1rem; color: #1e293b; }
.badge { font-size: 0.75rem; font-weight: bold; padding: 0.2rem 0.5rem; border-radius: 4px; background: #e2e8f0; color: #475569; }
.card-metrics { display: flex; justify-content: space-between; margin-bottom: 0.8rem; }
.metric label { display: block; font-size: 0.75rem; color: #64748b; }
.metric strong { font-size: 0.95rem; color: #0f172a; }
.progress-bar-bg { height: 6px; background: #f1f5f9; border-radius: 3px; overflow: hidden; margin-bottom: 0.8rem; }
.progress-bar-fill { height: 100%; background: #3b82f6; transition: width 0.3s ease; }
.tier-warning .progress-bar-fill { background: #eab308; }
.tier-critical .progress-bar-fill { background: #ef4444; }
.card-footer { display: flex; justify-content: flex-end; gap: 0.8rem; }
.btn-text { background: none; border: none; color: #2563eb; cursor: pointer; font-size: 0.85rem; font-weight: 500; }
.btn-text:hover { text-decoration: underline; }
</style>
