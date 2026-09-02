# ⚡ Demo 04: Node.js CommonJS & Express 4 Payment Gateway

## 1. 赛道说明
- **迁移类型**：Node.js CommonJS + 回调嵌套 ➔ 原生 ESM + Fastify & Node.js 24 LTS + TypeScript
- **核心技术债特征**：
  1. 使用 CommonJS 规范（`require()`, `module.exports = ...`）；
  2. 深度嵌套的错误优先回调模式（Error-First Callbacks：`fs.readFile`, `crypto.pbkdf2`, `fs.writeFile`）；
  3. 老旧的 `Express 4` 与 `body-parser` 传统中间件模型；
  4. 缺少 Promise / Async-Await 流程控制与类型定义。

## 2. 现代化目标
- **Node.js 24 原生 ESM**：`import { readFile } from 'node:fs/promises'`；
- **TypeScript 强类型**：`interface PaymentRequest`, `interface TransactionRecord`；
- **现代高并发微服务框架**：迁移至 `Fastify` 或 `NestJS` 原生异步插件模式；
- **现代化加解密与工具流**：使用 `node:crypto` 的 Promise API 替代老旧回调。
