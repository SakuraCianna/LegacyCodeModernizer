# 🌐 Demo 03: Vue 2 Enterprise FinTech CRM & Credit Facility Portal

## 1. 业务场景说明
这是一个面向金融机构的**企业级客户信贷额度评估、敞口监控与财务流水台账系统**。系统涵盖：
1. **企业信贷敞口与评级监控网格**（Credit Score Cards、动态额度利用率进度条、风险等级动态降级告警、防重复提交指令 `v-debounce`）；
2. **复式记账财务总账明细**（借记/贷记交易流水、精准浮点数金融计算器 `moneyCalculator.js`、多维动态组合过滤与弹窗记账）；
3. **Vuex 3 模块化状态树与悲观回滚**（`customer.js` 与 `ledger.js` 子模块联动、金额变动时联动重算敞口与风控评级）；
4. **合规审计日志与多格式导出**（CSV/JSON 导出确认模态框、跨组件全局 `EventBus` 广播）。

## 2. 遗留技术债与坏味道 (Antipatterns)
- **Options API 碎片化**：`data`, `computed`, `methods`, `watch` 散落在各个配置对象中，组件逻辑复杂时上下跳转难以维护；
- **废弃的全局 Filter**：依赖 Vue 2 `| currency`、`| dateSimple` 管道过滤器，在 Vue 3 中已被完全废弃；
- **隐式 EventBus 广播**：使用 `$eventBus.$emit` / `$on` 进行事件传递，缺乏静态追踪，容易引起内存泄漏；
- **缺少 TypeScript 静态约束**：纯 JavaScript 编写，Vuex State / Getters / Mutations 均为无类型字符串，重构极易发生运行时错误；
- **Webpack 慢速打包**：采用 Vue CLI 4 (Webpack 4) 构建体系。

## 3. 现代化目标 (Target Stack)
- **Vue 3.5 `<script setup lang="ts">`**：组合式 API + TypeScript 接口（`Customer`, `LedgerTransaction`）；
- **Pinia 2 状态管理**：使用 `useCustomerStore()` 与 `useLedgerStore()` 替换老旧的 Vuex 3 模块；
- **纯函数工具替换 Filter**：将全局过滤器平滑迁移至强类型格式化函数；
- **Vite 6 极速构建**：秒级冷启动与类型检查。
