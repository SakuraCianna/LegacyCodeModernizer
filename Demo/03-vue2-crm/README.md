# 🌐 Demo 03: Vue 2 Enterprise CRM Customer Portal

## 1. 赛道说明
- **迁移类型**：Vue 2.6 Options API + Vuex 3 ➔ Vue 3.5 `<script setup lang="ts">` + Pinia + Vite 6
- **核心技术债特征**：
  1. 使用老旧的 Options API（`data()`, `computed`, `methods`, `watch` 配置对象）；
  2. 使用 Vue 2 已弃用的全局 Filter（`| currency`, `| formatDate`）；
  3. 使用全局原型挂载的 EventBus（`Vue.prototype.$eventBus`）进行隐式事件广播；
  4. 使用老旧的 Vuex 3 集中式 Store（字符串 Mutation 常量，缺乏 TypeScript 强类型支持）；
  5. 采用 Webpack / Vue CLI 慢速构建体系。

## 2. 现代化目标
- **Vue 3 `<script setup lang="ts">`**：组合式 API、`defineProps<Props>()` 与 `defineEmits`；
- **Pinia 状态管理**：`useCustomerStore()`，提供完整的 TypeScript 类型推导；
- **原生格式化工具函数**：替代已废弃的 Vue Filter；
- **Vite 6 极速构建**：秒级冷启动与 HMR 热更新。
