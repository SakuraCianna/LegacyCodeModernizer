<template>
  <div id="app" class="crm-app">
    <header class="navbar">
      <h1>Enterprise CRM System (Vue 2 Legacy)</h1>
      <span class="user-status">Logged in as: {{ currentUser }}</span>
    </header>
    <main class="content-body">
      <CustomerList />
    </main>
  </div>
</template>

<script>
import CustomerList from './views/CustomerList.vue';

export default {
  name: 'App',
  components: {
    CustomerList
  },
  data() {
    return {
      currentUser: 'Admin Manager'
    };
  }
};
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #334155; }
.crm-app { min-height: 100vh; display: flex; flex-direction: column; }
.navbar { background-color: #1e293b; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
.content-body { padding: 2rem; flex: 1; }
</style>
