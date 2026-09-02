# 🎯 Modernization Benchmark Demo Presets (全量预置靶场资产库)

本目录为 **Legacy Code Modernizer** 预置的 5 大高质量真实企业级遗留系统靶场，供黑客松答辩、1-Click 一键演示与基准测试评测使用。

---

## 📂 靶场目录导航

| 靶场编号与名称 | 业务场景与领域 | 原始老技术栈 (Legacy Source) | 现代化升级目标 (Target Stack) | 核心技术债特征 |
| :--- | :--- | :--- | :--- | :--- |
| [**`01-jsp-ecommerce`**](./01-jsp-ecommerce/) | 电商购物车与结账单体系统 | JSP 2.0、Servlet 3.1、原生 JDBC、Java 8、`HttpSession` | Spring Boot 3.4 REST API、Java 21 Record DTO、无状态 JWT 鉴权 | 嵌入式 Java Scriptlet、SQL 拼接注入隐患、有状态 Session |
| [**`02-python2-etl`**](./02-python2-etl/) | 金融交易数据抓取与批处理流水线 | Python 2.7、`urllib2`、`Queue.Queue`、`cPickle`、`xrange` | Python 3.12+、`httpx` 异步并发、`asyncio`、`pydantic` 强类型 | 废弃标准库、字符串/字节混淆、阻塞式 I/O、无类型注解 |
| [**`03-vue2-crm`**](./03-vue2-crm/) | 企业 CRM 客户账单与状态门户 | Vue 2.6、Options API、Vuex 3、全局 Filter、EventBus | Vue 3.5 `<script setup lang="ts">`、Pinia 2、Vite 6 构建 | 废弃的全局过滤器、隐式 EventBus 广播、无 TypeScript 支持 |
| [**`04-node-cjs-microservice`**](./04-node-cjs-microservice/) | 支付凭证签名与异步落盘微服务 | Node.js CommonJS、Express 4、回调金字塔 (`crypto.pbkdf2`) | Node.js 24 LTS 原生 ESM、Fastify 插件架构、Async/Await | 错误优先回调地狱、无 Promise 异步编排、老旧中间件模型 |
| [**`05-springboot2-vue2-fullstack`**](./05-springboot2-vue2-fullstack/) | 企业智能仓库与进销存管理系统 (WMS) | Spring Boot 2.7 (`javax.*`)、Java 8 POJO、Vue 2.7 Webpack | Spring Boot 3.4 (`jakarta.*`)、Java 21、Vue 3.5 TS + Vite 6 | 前后端分离企业单体、`javax` 阻碍 JDK 21 升级、Options API |

---

## 🛡️ 使用规范
- 本目录下的所有文件均为**只读测试基准资产（Read-Only Fixtures）**；
- 在工作台运行时，系统会将选定的 Demo 复制到会话隔离目录 `/workspaces/:username/:sessionId/source/` 下作为基准黄金真理源，绝不破坏本目录中的初始资产。
