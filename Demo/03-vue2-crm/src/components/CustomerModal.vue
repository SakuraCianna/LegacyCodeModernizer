<template>
  <div class="modal-backdrop">
    <div class="modal-box">
      <h3>Add New Enterprise Client & Credit Facility</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-item">
          <label>Company Name:</label>
          <input type="text" v-model="form.name" placeholder="e.g. Acme Holdings Corp" required />
        </div>
        <div class="form-item">
          <label>Billing Contact Email:</label>
          <input type="email" v-model="form.email" placeholder="billing@acme.com" required />
        </div>
        <div class="form-row">
          <div class="form-item flex-1">
            <label>Approved Credit Limit ($):</label>
            <input type="number" v-model.number="form.creditLimit" min="1000" step="500" required />
          </div>
          <div class="form-item flex-1">
            <label>Initial Exposure ($):</label>
            <input type="number" v-model.number="form.currentBalance" min="0" step="100" required />
          </div>
        </div>
        <div class="form-row">
          <div class="form-item flex-1">
            <label>Risk Rating:</label>
            <select v-model="form.riskRating" class="select-box">
              <option value="A">Grade A (Prime / Low Risk)</option>
              <option value="B">Grade B (Standard)</option>
              <option value="C">Grade C (Subprime / Caution)</option>
              <option value="D">Grade D (High Risk / Secured Only)</option>
            </select>
          </div>
          <div class="form-item flex-1">
            <label>Enterprise Tier:</label>
            <select v-model="form.tier" class="select-box">
              <option value="STANDARD">Standard</option>
              <option value="GROWTH">Growth</option>
              <option value="PRO">Pro</option>
              <option value="ENTERPRISE">Enterprise Tier</option>
            </select>
          </div>
        </div>

        <div class="modal-actions">
          <button type="button" @click="$emit('close')" class="btn-cancel">Cancel</button>
          <button type="submit" class="btn-submit" v-debounce="{ handler: handleSubmit, delay: 800 }">
            Save Client Facility
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CustomerModal',
  data() {
    return {
      form: {
        name: '',
        email: '',
        creditLimit: 25000.0,
        currentBalance: 0.0,
        riskRating: 'A',
        tier: 'GROWTH',
        status: 'ACTIVE'
      }
    };
  },
  methods: {
    handleSubmit() {
      if (!this.form.name || !this.form.email) {
        alert('Please complete all mandatory fields.');
        return;
      }
      if (this.form.currentBalance > this.form.creditLimit) {
        alert('Initial exposure cannot exceed approved credit limit.');
        return;
      }
      this.$emit('save', Object.assign({}, this.form));
    }
  }
};
</script>

<style scoped>
.modal-backdrop { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-box { background: white; padding: 2rem; border-radius: 8px; width: 500px; box-shadow: 0 4px 10px rgba(0,0,0,0.15); }
.modal-box h3 { margin-bottom: 1.2rem; color: #0f172a; font-size: 1.2rem; }
.form-item { margin-bottom: 1rem; }
.form-item label { display: block; margin-bottom: 0.3rem; font-size: 0.85rem; font-weight: 600; color: #475569; }
.form-item input, .select-box { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.9rem; }
.form-row { display: flex; gap: 1rem; }
.flex-1 { flex: 1; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.8rem; margin-top: 1.5rem; }
.btn-cancel { padding: 0.6rem 1.2rem; background: #e2e8f0; border: none; border-radius: 4px; cursor: pointer; }
.btn-submit { padding: 0.6rem 1.2rem; background: #2563eb; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 600; }
</style>
