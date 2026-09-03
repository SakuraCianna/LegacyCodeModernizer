# 🎯 Modernization Benchmark Demo Presets (全量生产级真实遗留靶场资产库)

本目录为 **Legacy Code Modernizer** 精心打造的 5 套**深度结合真实生产场景、具备复杂业务逻辑、高并发/限流/防呆设计且代码行数均超 1000+ 行**的现代化重构靶场资产包。

---

## 📂 靶场目录全景导航

| 靶场编号与工程目录 | 核心业务场景 | 生产级设计（并发/限流/防呆） | 遗留技术栈 (Legacy Source) | 现代化升级目标 (Target Stack) | 代码规模与文件数 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| [**`01-jsp-ecommerce`**](./01-jsp-ecommerce/) | 电商购物车、阶梯折扣定价、分步结账与订单交易引擎 | • 行级悲观锁 `FOR UPDATE` 超卖防护<br>• `X-Idempotency-Key` 幂等防重提交<br>• IP 令牌桶滑动窗口限流 Filter<br>• 优惠券库存原子扣减与事务回滚 | JSP 2.0、Servlet 3.1、原生 JDBC、MySQL、Java 8、`HttpSession` | Spring Boot 3.4 REST API、Java 21 Record DTO、无状态 JWT 鉴权、Spring Data JPA | **19 个源文件<br>> 1,200+ 行代码** |
| [**`02-python2-etl`**](./02-python2-etl/) | 高频金融外汇折算、VaR 风险价值度量与多线程批处理流水线 | • 线程安全 Token Bucket 接口限流<br>• 零除保护与负金额防御<br>• 多线程队列 Consumer + 哨兵停机<br>• 线程写锁审计日志与 cPickle 归档 | Python 2.7、`urllib2`、`Queue.Queue`、`cPickle`、`xrange`、`unittest` | Python 3.12+、`httpx` 异步并发、`asyncio`、`pydantic` 强类型数据契约 | **13 个源文件<br>> 1,050+ 行代码** |
| [**`03-vue2-crm`**](./03-vue2-crm/) | 企业金融客户信贷敞口评估、复式记账台账与风控门户 | • `v-debounce` 防手抖双击重复提交<br>• 高精度金融浮点运算器（防 `0.1+0.2` 误差）<br>• 信用额度超限自动降级告警<br>• Vuex 模块化状态联动与跨组件事件广播 | Vue 2.6、Options API、Vuex 3、全局 Filter、EventBus、Webpack 4 | Vue 3.5 `<script setup lang="ts">`、Pinia 2、Vite 6 极速构建体系 | **17 个源文件<br>> 1,150+ 行代码** |
| [**`04-node-cjs-microservice`**](./04-node-cjs-microservice/) | 跨境支付扣款授权、批量清结算与银行流水对账网关 | • HMAC-SHA256 防篡改验签中间件<br>• TTL 缓存幂等拦截器<br>• 内存滑动窗口限流器<br>• 双向银行对账差异分析与自动化单测 | Node.js CommonJS、Express 4、回调金字塔 (`crypto.pbkdf2`)、JSON 持久化 | Node.js 24 LTS 原生 ESM、Fastify 插件架构、Async/Await、TypeScript | **16 个源文件<br>> 1,100+ 行代码** |
| [**`05-springboot2-vue2-fullstack`**<br>*(旗帜级核心全栈靶场)* | **企业级支付收银台、钱包余额扣减、防重退款状态机与对账总账系统** | **• Jedis Redis 分布式锁 (SETNX + Lua 原子解锁)<br>• Redis 5 req/s 滑动窗口限流器<br>• 账户钱包 `@Version` 乐观锁 CAS 扣款<br>• 多次部分退款与累计超限防呆校验<br>• 财务审计流水快照 (before/delta/after)<br>• 完整 JUnit 自动化集成测试套件** | **后端：Spring Boot 2.7.18 (`javax.*`)、Java 8、Jedis Pool、MySQL/H2、Spring Data JPA<br>前端：Vue 2.7 Options API、Vuex 3、Webpack 4、Axios** | **后端：Spring Boot 3.4 (`jakarta.*`)、Java 21 Record DTO、Spring Data Redis Lettuce<br>前端：Vue 3.5 `<script setup lang="ts">`、Pinia 2、Vite 6** | **26 个源文件<br>> 2,200+ 行代码** |

---

## 🛡️ 靶场核心设计原则
1. **真实工业级业务厚度**：彻底告别简单的 HelloWorld 玩具代码，每个 Demo 均具备完整的领域实体、持久化 DAO/Repository、业务 Service、控制器/视图及数据契约；
2. **保留生产级防护，重构遗留坏味道**：
   - **去除**：`javax.*` 废弃命名空间、Jedis 手动释放连接、嵌套回调地狱、Vue 2 Options API 冗长配置、纯动态无类型字典；
   - **保留并升级**：分布式锁并发互斥、账户余额乐观锁、幂等 Token 校验、滑动窗口限流、防呆边界检查与自动化测试断言；
3. **沙箱行为等价性验证**：每个靶场均配备完善的测试用例（`PaymentServiceIntegrationTest.java`, `test_etl_pipeline.py`, `payment.test.js` 等），保证现代化重构前后测试 100% 绿灯通过！
