<template>
  <div class="payment-cashier-view">
    <div class="top-row">
      <div class="wallet-card">
        <label>Available Wallet Balance</label>
        <div class="balance-display">
          <h2>${{ walletBalance.toFixed(2) }}</h2>
          <button @click="showDepositModal = true" class="btn btn-sm btn-deposit">+ Topup Deposit</button>
        </div>
      </div>
      <div class="create-order-card">
        <h3>Create Simulated Purchase Order</h3>
        <form @submit.prevent="handleCreateOrder" class="inline-form">
          <input type="text" v-model="newTitle" placeholder="Item / Service Title" required />
          <input type="number" v-model.number="newAmount" min="0.01" step="0.01" placeholder="Amount ($)" required />
          <button type="submit" class="btn btn-primary">Create Order</button>
        </form>
      </div>
    </div>

    <h3>Pending & Completed Payment Orders</h3>
    <table class="order-table">
      <thead>
        <tr>
          <th>Order No</th>
          <th>Description</th>
          <th>Amount</th>
          <th>Refunded</th>
          <th>Status</th>
          <th>Created Date</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.id">
          <td><code>{{ order.orderNo }}</code></td>
          <td><strong>{{ order.title }}</strong></td>
          <td>${{ Number(order.totalAmount).toFixed(2) }}</td>
          <td>${{ Number(order.refundedAmount).toFixed(2) }}</td>
          <td>
            <span :class="['status-badge', order.status.toLowerCase()]">{{ order.status }}</span>
          </td>
          <td>{{ order.createdAt }}</td>
          <td>
            <button
              v-if="order.status === 'CREATED'"
              @click="openCashierModal(order)"
              class="btn btn-sm btn-pay"
            >
              Pay Now
            </button>
            <span v-else class="text-muted">Settled</span>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Cashier Modal -->
    <div v-if="selectedOrder" class="modal-overlay">
      <div class="modal-box">
        <h3>Cashier Desk: Order Payment</h3>
        <p class="order-summary">Order: <strong>{{ selectedOrder.orderNo }}</strong></p>
        <p class="order-summary">Title: {{ selectedOrder.title }}</p>
        <p class="order-total">Total Due: <strong>${{ Number(selectedOrder.totalAmount).toFixed(2) }}</strong></p>

        <div class="channel-selector">
          <label class="channel-option">
            <input type="radio" value="WALLET_BALANCE" v-model="selectedChannel" />
            <span>💳 Account Wallet Balance (Avail: ${{ walletBalance.toFixed(2) }})</span>
          </label>
          <label class="channel-option">
            <input type="radio" value="ALIPAY" v-model="selectedChannel" />
            <span>🔵 Alipay Cross-Border Gateway</span>
          </label>
          <label class="channel-option">
            <input type="radio" value="WECHAT_PAY" v-model="selectedChannel" />
            <span>🟢 WeChat Pay Direct</span>
          </label>
        </div>

        <div class="modal-actions">
          <button @click="selectedOrder = null" class="btn btn-secondary">Cancel</button>
          <button
            @click="submitPayment"
            class="btn btn-success"
            :disabled="isPaying || (selectedChannel === 'WALLET_BALANCE' && walletBalance < selectedOrder.totalAmount)"
          >
            {{ isPaying ? 'Authorizing & Locking...' : 'Confirm Payment' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Deposit Modal -->
    <div v-if="showDepositModal" class="modal-overlay">
      <div class="modal-box">
        <h3>Deposit to Wallet</h3>
        <input type="number" v-model.number="depositAmount" min="10" step="10" class="input-full" placeholder="Deposit Amount ($)" />
        <div class="modal-actions">
          <button @click="showDepositModal = false" class="btn btn-secondary">Cancel</button>
          <button @click="handleDeposit" class="btn btn-primary">Topup</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import api from '../services/paymentApi';

export default {
  name: 'PaymentCashier',
  data() {
    return {
      newTitle: '',
      newAmount: 99.00,
      selectedOrder: null,
      selectedChannel: 'WALLET_BALANCE',
      isPaying: false,
      showDepositModal: false,
      depositAmount: 500.00
    };
  },
  computed: {
    ...mapState(['currentUserId', 'walletBalance', 'orders'])
  },
  methods: {
    handleCreateOrder() {
      api.createOrder(this.currentUserId, this.newTitle, this.newAmount).then(order => {
        this.$store.commit('ADD_ORDER', order);
        this.newTitle = '';
      }).catch(err => {
        alert('Order creation failed: ' + err.message);
      });
    },
    openCashierModal(order) {
      this.selectedOrder = order;
      this.selectedChannel = 'WALLET_BALANCE';
    },
    submitPayment() {
      if (!this.selectedOrder) return;
      this.isPaying = true;
      const idempotencyToken = 'IDEM-' + Date.now() + '-' + Math.floor(Math.random() * 10000);

      api.payOrder({
        orderNo: this.selectedOrder.orderNo,
        userId: this.currentUserId,
        amount: this.selectedOrder.totalAmount,
        channel: this.selectedChannel,
        idempotencyToken: idempotencyToken
      }).then(res => {
        this.isPaying = false;
        this.$store.commit('UPDATE_ORDER_STATUS', { orderNo: res.orderNo, status: res.status });
        this.$store.dispatch('refreshWallet');
        this.selectedOrder = null;
        alert('Payment processed successfully! Order status: ' + res.status);
      }).catch(err => {
        this.isPaying = false;
        alert('Payment Authorization Failed: ' + err.message);
      });
    },
    handleDeposit() {
      api.depositWallet(this.currentUserId, this.depositAmount, 'User Topup').then(w => {
        this.$store.commit('SET_WALLET_BALANCE', Number(w.balance));
        this.showDepositModal = false;
      });
    }
  },
  mounted() {
    this.$store.dispatch('refreshWallet');
    this.$store.dispatch('refreshOrders');
  }
};
</script>

<style scoped>
.payment-cashier-view { padding-top: 0.5rem; }
.top-row { display: flex; gap: 1.5rem; margin-bottom: 2rem; }
.wallet-card { background: white; padding: 1.5rem; border-radius: 8px; flex: 1; box-shadow: 0 1px 3px rgba(0,0,0,0.1); border-left: 4px solid #16a34a; }
.wallet-card label { font-size: 0.85rem; color: #64748b; font-weight: 600; text-transform: uppercase; }
.balance-display { display: flex; justify-content: space-between; align-items: center; margin-top: 0.5rem; }
.balance-display h2 { font-size: 2rem; color: #16a34a; }
.create-order-card { background: white; padding: 1.5rem; border-radius: 8px; flex: 2; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.create-order-card h3 { font-size: 1rem; color: #334155; margin-bottom: 0.8rem; }
.inline-form { display: flex; gap: 0.8rem; }
.inline-form input { padding: 0.6rem 0.8rem; border: 1px solid #cbd5e1; border-radius: 6px; }
.inline-form input:first-child { flex: 2; }
.inline-form input:nth-child(2) { flex: 1; }
.btn { padding: 0.6rem 1.2rem; border-radius: 6px; border: none; cursor: pointer; font-weight: 500; font-size: 0.9rem; }
.btn-primary { background: #2563eb; color: white; }
.btn-secondary { background: #e2e8f0; color: #334155; }
.btn-success { background: #16a34a; color: white; }
.btn-pay { background: #ea580c; color: white; }
.btn-deposit { background: #dcfce7; color: #166534; border: 1px solid #86efac; }
.btn-sm { padding: 0.4rem 0.8rem; font-size: 0.85rem; }
.order-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-top: 1rem; }
.order-table th, .order-table td { padding: 1rem; text-align: left; border-bottom: 1px solid #f1f5f9; }
.order-table th { background: #f8fafc; font-weight: 600; color: #475569; }
.status-badge { padding: 0.25rem 0.6rem; border-radius: 9999px; font-size: 0.75rem; font-weight: bold; }
.status-badge.created { background: #fef9c3; color: #854d0e; }
.status-badge.success { background: #dcfce7; color: #166534; }
.status-badge.refund_partial { background: #e0f2fe; color: #0369a1; }
.status-badge.refund_full { background: #f1f5f9; color: #64748b; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-box { background: white; padding: 2rem; border-radius: 8px; width: 480px; }
.order-summary { font-size: 0.9rem; color: #475569; margin-bottom: 0.4rem; }
.order-total { font-size: 1.2rem; color: #0f172a; margin: 1rem 0; }
.channel-selector { display: flex; flex-direction: column; gap: 0.8rem; margin: 1.2rem 0; }
.channel-option { display: flex; align-items: center; gap: 0.6rem; font-size: 0.9rem; cursor: pointer; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.8rem; margin-top: 1.5rem; }
.input-full { width: 100%; padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 6px; margin-top: 1rem; }
</style>
