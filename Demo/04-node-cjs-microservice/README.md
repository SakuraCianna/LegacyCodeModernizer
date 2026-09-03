# ⚡ Demo 04: Node.js CommonJS & Express 4 Payment Settlement Gateway

## 1. 业务场景说明
这是一个高安全等级的**跨境支付扣款、批量清结算与银行流水对账微服务**。系统包含：
1. **支付鉴权与验签中间件**（HMAC-SHA256 防篡改签名校验 `signatureValidator.js`、幂等性 Token 拦截 `idempotency.js`、IP 级别滑动窗口限流器 `rateLimiter.js`）；
2. **扣款与手续费计算引擎**（PBKDF2 密文推导、费率模型计算 `chargeService.js`、多层回调金字塔）；
3. **批量清算与状态机流转**（`settlementService.js`、批量事务状态更新）；
4. **银行流水对账差异检测**（`reconciliation.js`、双向差异比对报告）；
5. **单元测试与集成测试断言**（`tests/payment.test.js`）。

## 2. 遗留技术债与坏味道 (Antipatterns)
- **回调地狱 (Callback Hell)**：深层嵌套的错误优先回调（`fs.readFile` ➔ `crypto.pbkdf2` ➔ `fs.writeFile`），难以阅读与异常捕获；
- **CommonJS 模块规范**：大量使用 `require()` 与 `module.exports`，无法利用 Node 24 ESM 的静态优化与 Top-level Await；
- **缺乏类型安全**：缺少 TypeScript 契约，入参和返回值易发生类型漂移；
- **老旧中间件模型**：基于传统的 Express 4 同步中间件模型。

## 3. 现代化目标 (Target Stack)
- **Node.js 24 LTS 原生 ESM**：`import { readFile, writeFile } from 'node:fs/promises'`；
- **Promise & Async/Await**：彻底消除回调金字塔，引入结构化 `try/catch`；
- **Fastify 现代异步架构**：高并发异步 Hook 与 Schema 校验；
- **TypeScript 强类型**：严格的 `interface PaymentChargeRequest` 与 DTO 定义。
