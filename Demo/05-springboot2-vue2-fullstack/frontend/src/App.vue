<template>
  <div id="app" class="wms-app">
    <header class="navbar">
      <h1>Smart WMS - Enterprise Warehouse (Spring Boot 2.7 + Vue 2)</h1>
    </header>
    <main>
      <InventoryDashboard />
    </main>
  </div>
</template>

<script>
import InventoryDashboard from './views/InventoryDashboard.vue';

export default {
  name: 'App',
  components: {
    InventoryDashboard
  }
};
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; background-color: #f1f5f9; color: #1e293b; }
.navbar { background: #0f172a; color: white; padding: 1rem 2rem; }
</style>
