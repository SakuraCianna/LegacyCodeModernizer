<template>
  <div class="modal-backdrop">
    <div class="modal-box">
      <h3>Add New Enterprise Client</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-item">
          <label>Company Name:</label>
          <input type="text" v-model="form.name" required />
        </div>
        <div class="form-item">
          <label>Contact Email:</label>
          <input type="email" v-model="form.contact" required />
        </div>
        <div class="form-item">
          <label>Initial Balance ($):</label>
          <input type="number" v-model.number="form.balance" min="0" step="0.01" required />
        </div>
        <div class="modal-actions">
          <button type="button" @click="$emit('close')" class="btn-cancel">Cancel</button>
          <button type="submit" class="btn-submit">Save Client</button>
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
        contact: '',
        balance: 0.0,
        status: 'ACTIVE',
        joinedAt: new Date().toISOString().split('T')[0]
      }
    };
  },
  methods: {
    handleSubmit() {
      if (!this.form.name || !this.form.contact) {
        alert('Please complete all required fields');
        return;
      }
      this.$emit('save', Object.assign({}, this.form));
    }
  }
};
</script>

<style scoped>
.modal-backdrop { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-box { background: white; padding: 2rem; border-radius: 8px; width: 450px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
.modal-box h3 { margin-bottom: 1.5rem; color: #1e293b; }
.form-item { margin-bottom: 1rem; }
.form-item label { display: block; margin-bottom: 0.4rem; font-size: 0.9rem; color: #475569; }
.form-item input { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 4px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.8rem; margin-top: 1.5rem; }
.btn-cancel { padding: 0.6rem 1.2rem; background: #e2e8f0; border: none; border-radius: 4px; cursor: pointer; }
.btn-submit { padding: 0.6rem 1.2rem; background: #2563eb; color: white; border: none; border-radius: 4px; cursor: pointer; }
</style>
