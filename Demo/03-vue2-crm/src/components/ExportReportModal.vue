<template>
  <div class="modal-backdrop">
    <div class="modal-box">
      <h3>Export Financial Audit Ledger</h3>
      <p class="subtitle">Select report format and scope for compliance archiving.</p>

      <div class="form-group">
        <label>Format:</label>
        <div class="radio-group">
          <label><input type="radio" value="CSV" v-model="selectedFormat" /> CSV Spreadsheet</label>
          <label><input type="radio" value="JSON" v-model="selectedFormat" /> JSON Stream</label>
          <label><input type="radio" value="PDF" v-model="selectedFormat" /> PDF Summary</label>
        </div>
      </div>

      <div class="form-group">
        <label>Date Range Filter:</label>
        <select v-model="dateRange" class="form-control">
          <option value="CURRENT_MONTH">Current Month (September 2023)</option>
          <option value="Q3_2023">Q3 2023 Full Quarter</option>
          <option value="YTD">Year to Date (2023)</option>
        </select>
      </div>

      <div class="modal-actions">
        <button @click="$emit('close')" class="btn btn-secondary">Cancel</button>
        <button @click="handleExport" class="btn btn-primary" :disabled="isExporting">
          {{ isExporting ? 'Generating Report...' : 'Download Report' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ExportReportModal',
  data() {
    return {
      selectedFormat: 'CSV',
      dateRange: 'CURRENT_MONTH',
      isExporting: false
    };
  },
  methods: {
    handleExport() {
      this.isExporting = true;
      setTimeout(() => {
        this.isExporting = false;
        alert('Financial report exported successfully as ' + this.selectedFormat);
        this.$emit('close');
      }, 800);
    }
  }
};
</script>

<style scoped>
.modal-backdrop { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-box { background: white; padding: 2rem; border-radius: 8px; width: 440px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
.subtitle { font-size: 0.85rem; color: #64748b; margin-bottom: 1.2rem; }
.form-group { margin-bottom: 1.2rem; }
.form-group label { display: block; margin-bottom: 0.4rem; font-size: 0.9rem; font-weight: 600; color: #334155; }
.radio-group { display: flex; gap: 1rem; font-size: 0.9rem; }
.form-control { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 4px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.8rem; margin-top: 1.5rem; }
.btn { padding: 0.6rem 1.2rem; border-radius: 4px; border: none; cursor: pointer; font-weight: 500; }
.btn-secondary { background: #e2e8f0; color: #334155; }
.btn-primary { background: #2563eb; color: white; }
.btn-primary:disabled { background: #94a3b8; cursor: not-allowed; }
</style>
