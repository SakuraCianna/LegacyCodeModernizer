<template>
  <div class="inventory-dashboard">
    <div class="alert-banner" v-if="lowStockItems.length > 0">
      ⚠️ <strong>Attention:</strong> {{ lowStockItems.length }} item(s) are below safety stock threshold!
    </div>

    <div class="header-section">
      <h2>Warehouse Inventory Management</h2>
      <button @click="showAddModal = true" class="btn btn-success">+ Add SKU</button>
    </div>

    <div class="table-container">
      <table class="inventory-table">
        <thead>
          <tr>
            <th>SKU Code</th>
            <th>Item Name</th>
            <th>Category</th>
            <th>Location Zone</th>
            <th>Unit Price ($)</th>
            <th>Current Quantity</th>
            <th>Stock Control</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td><code>{{ item.skuCode }}</code></td>
            <td><strong>{{ item.itemName }}</strong></td>
            <td>{{ item.category }}</td>
            <td>{{ item.locationZone }}</td>
            <td>${{ item.unitPrice.toFixed(2) }}</td>
            <td>
              <span :class="['qty-badge', item.quantity <= 10 ? 'low' : 'normal']">
                {{ item.quantity }} units
              </span>
            </td>
            <td>
              <button @click="changeStock(item, 1)" class="btn btn-sm btn-outline">+</button>
              <button @click="changeStock(item, -1)" class="btn btn-sm btn-outline" :disabled="item.quantity <= 0">-</button>
            </td>
            <td>
              <button @click="removeItem(item.id)" class="btn btn-sm btn-danger">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Legacy Add SKU Modal -->
    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Create New Inventory Item</h3>
        <form @submit.prevent="submitCreate">
          <div class="form-row">
            <label>SKU Code:</label>
            <input type="text" v-model="newItem.skuCode" required />
          </div>
          <div class="form-row">
            <label>Item Name:</label>
            <input type="text" v-model="newItem.itemName" required />
          </div>
          <div class="form-row">
            <label>Category:</label>
            <input type="text" v-model="newItem.category" required />
          </div>
          <div class="form-row">
            <label>Zone / Bin:</label>
            <input type="text" v-model="newItem.locationZone" placeholder="Zone-A1" required />
          </div>
          <div class="form-row">
            <label>Unit Price ($):</label>
            <input type="number" v-model.number="newItem.unitPrice" step="0.01" required />
          </div>
          <div class="form-row">
            <label>Initial Quantity:</label>
            <input type="number" v-model.number="newItem.quantity" min="0" required />
          </div>
          <div class="modal-actions">
            <button type="button" @click="showAddModal = false" class="btn">Cancel</button>
            <button type="submit" class="btn btn-success">Save SKU</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api';

export default {
  name: 'InventoryDashboard',
  data() {
    return {
      items: [],
      lowStockItems: [],
      showAddModal: false,
      newItem: {
        skuCode: '',
        itemName: '',
        category: 'Electronics',
        locationZone: 'Zone-A1',
        unitPrice: 19.99,
        quantity: 100
      }
    };
  },
  methods: {
    loadData() {
      api.getInventoryItems().then(data => {
        this.items = data;
      });
      api.getLowStockAlerts(10).then(alerts => {
        this.lowStockItems = alerts;
      });
    },
    changeStock(item, delta) {
      api.adjustStock(item.id, delta).then(updated => {
        item.quantity = updated.quantity;
        this.loadData();
      });
    },
    removeItem(id) {
      if (confirm('Are you sure you want to remove this SKU?')) {
        api.deleteItem(id).then(() => {
          this.loadData();
        });
      }
    },
    submitCreate() {
      api.createItem(this.newItem).then(() => {
        this.showAddModal = false;
        this.newItem = { skuCode: '', itemName: '', category: 'Electronics', locationZone: 'Zone-A1', unitPrice: 19.99, quantity: 100 };
        this.loadData();
      });
    }
  },
  mounted() {
    // Initial load mock data
    this.items = [
      { id: 1, skuCode: 'SKU-ELEC-001', itemName: 'Wireless Barcode Scanner', category: 'Hardware', locationZone: 'Zone-A1', unitPrice: 89.50, quantity: 4 },
      { id: 2, skuCode: 'SKU-PKG-002', itemName: 'Thermal Shipping Labels (1000/roll)', category: 'Packaging', locationZone: 'Zone-B3', unitPrice: 12.00, quantity: 150 },
      { id: 3, skuCode: 'SKU-TOOL-003', itemName: 'Industrial Tape Dispenser', category: 'Tools', locationZone: 'Zone-C2', unitPrice: 24.90, quantity: 8 }
    ];
    this.lowStockItems = this.items.filter(i => i.quantity <= 10);
  }
};
</script>

<style scoped>
.inventory-dashboard { padding: 1.5rem; }
.alert-banner { background: #fef2f2; border: 1px solid #f87171; color: #991b1b; padding: 1rem; border-radius: 6px; margin-bottom: 1.5rem; }
.header-section { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.table-container { background: white; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); overflow: hidden; }
.inventory-table { width: 100%; border-collapse: collapse; text-align: left; }
.inventory-table th, .inventory-table td { padding: 1rem; border-bottom: 1px solid #f1f5f9; }
.inventory-table th { background: #f8fafc; font-weight: 600; color: #475569; }
.qty-badge { padding: 0.25rem 0.6rem; border-radius: 4px; font-weight: bold; font-size: 0.85rem; }
.qty-badge.normal { background: #e0f2fe; color: #0369a1; }
.qty-badge.low { background: #fee2e2; color: #dc2626; }
.btn { padding: 0.5rem 1rem; border-radius: 6px; border: none; cursor: pointer; font-weight: 500; }
.btn-success { background: #16a34a; color: white; }
.btn-sm { padding: 0.25rem 0.5rem; font-size: 0.85rem; }
.btn-outline { background: #f1f5f9; border: 1px solid #cbd5e1; margin-right: 0.3rem; }
.btn-danger { background: #ef4444; color: white; }
.modal-overlay { position: fixed; top:0; left:0; right:0; bottom:0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; }
.modal-content { background: white; padding: 2rem; border-radius: 8px; width: 450px; }
.form-row { margin-bottom: 1rem; }
.form-row label { display: block; margin-bottom: 0.3rem; font-size: 0.9rem; }
.form-row input { width: 100%; padding: 0.5rem; border: 1px solid #cbd5e1; border-radius: 4px; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1.5rem; }
</style>
