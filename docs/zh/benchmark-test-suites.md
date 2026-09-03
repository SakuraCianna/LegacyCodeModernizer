# 🧪 基准测试集与量化指标规范 (Benchmark Test Suites & Metrics)

<p align="center">
  <a href="../benchmark-test-suites.md">English Version</a> | <a href="./benchmark-test-suites.md">简体中文版</a>
</p>

---

## 1. 确定性数学保真度评分计算公式

为提供客观、可量化的技术评价标准，系统通过 **Test & Quality Verifier Agent** 对每次现代化重构计算客观、确定性的 **综合业务保真度评分** ($S_{\text{fidelity}} \in [0, 100\%]$)：

$$S_{\text{fidelity}} = 0.50 \times P_{\text{tests}} + 0.30 \times C_{\text{ast}} + 0.20 \times M_{\text{schema}}$$

其中各项量化指标定义如下：
- **$P_{\text{tests}}$（动态自动化测试通过率）**：
  $$P_{\text{tests}} = \frac{N_{\text{passed}}}{N_{\text{total\_tests}}} \times 100\%$$
  衡量在隔离沙箱（JUnit 5, PyTest, Vitest）中执行生成的回归断言用例的通过比例。
- **$C_{\text{ast}}$（公共 AST 符号保真覆盖率）**：
  $$C_{\text{ast}} = \frac{|\text{Symbols}_{\text{target}} \cap \text{Symbols}_{\text{source\_mapped}}|}{|\text{Symbols}_{\text{source\_public}}|} \times 100\%$$
  衡量原老旧系统中的所有 Public 函数、API 路由端点、DTO 属性及状态变更方法是否在现代化 AST 中完整映射保留。
- **$M_{\text{schema}}$（接口契约与数据模型一致性）**：
  $$M_{\text{schema}} = \frac{N_{\text{matching\_fields}}}{N_{\text{total\_contract\_fields}}} \times 100\%$$
  衡量重构前后的 HTTP 请求/响应 Payload JSON Schema 字段及类型的对齐程度。

### 1.1 工业级双轨测试验证分类体系 (Dual-Track Testing Strategy)

为精准还原真实企业级遗留系统的重构现状，系统将基准靶场资产划分为两类具备鲜明工业特征的测试验证轨道：

1. **第一轨：自带历史回归测试套件（Pre-existing Test Harness）**
   - **典型靶场**：`02-python2-etl`、`04-node-cjs-microservice`、`05-springboot2-vue2-fullstack`。
   - **工业场景**：企业核心服务中已存在高覆盖率的历史测试资产。现代化重构的核心诉求是在全面升级语言版本、核心中间件与依赖库（如 Java 8 ➔ 21、Jedis ➔ Lettuce、CJS ➔ ESM）后，**原有回归测试套件 100% 保持绿灯运行（Zero Behavioral Regression）**。
   - **验证机制**：隔离沙箱直接装载目标工程并执行原有用例，断言执行结果与业务行为等价性。

2. **第二轨：无单测遗留资产逆向自愈测试合成（Reverse Test Synthesis for Legacy）**
   - **典型靶场**：`01-jsp-ecommerce`、`03-vue2-crm`。
   - **工业场景**：大量历经十余年演进的单体 JSP 模板与老旧前端页面，历史测试完全缺失，严重依赖人工黑盒验证。
   - **验证机制**：**Test & Quality Verifier Agent** 基于老旧源源码的 AST 控制流（Control Flow Graph）、JSP/Servlet 参数校验逻辑与 Vue 2 数据流动，**自动逆向推导出等价的契约边界断言并合成现代测试套件**（如 Spring Boot MockMvc 集成测试、Vitest 组件单元测试），一举补齐企业缺失的历史测试资产。

---

## 2. 基准用例一：JSP ➔ Spring Boot 3 & REST API (`benchmark-jsp-ecommerce`)

### 2.1 业务场景说明
一个典型的老旧电商 Session 登录与购物车管理模块，包含 JSP 表单标签、脚本片段 Java 逻辑、原生 `HttpSession` 状态操作以及 SQL 拼接查询。

### 2.2 老旧源源码 (`source/backend/src/main/webapp/cart.jsp`)
```jsp
<%@ page import="java.sql.*, java.util.*" %>
<%
    String action = request.getParameter("action");
    HttpSession session = request.getSession();
    List<String> cart = (List<String>) session.getAttribute("CART_ITEMS");
    if (cart == null) {
        cart = new ArrayList<String>();
        session.setAttribute("CART_ITEMS", cart);
    }

    if ("add".equals(action)) {
        String itemId = request.getParameter("itemId");
        int quantity = Integer.parseInt(request.getParameter("qty"));
        
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop", "root", "pass");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT price, stock FROM items WHERE id = " + itemId);
        if (rs.next() && rs.getInt("stock") >= quantity) {
            cart.add(itemId + ":" + quantity);
            out.println("{\"status\":\"SUCCESS\",\"itemCount\":" + cart.size() + "}");
        } else {
            out.println("{\"status\":\"OUT_OF_STOCK\"}");
        }
        conn.close();
    }
%>
```

### 2.3 现代化目标源码 (`target/backend/src/main/java/com/modernizer/shop/controller/CartRestController.java`)
```java
package com.modernizer.shop.controller;

import com.modernizer.shop.dto.CartItemRequest;
import com.modernizer.shop.dto.CartResponse;
import com.modernizer.shop.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartRestController {

    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CartItemRequest request) {
        CartResponse response = cartService.addItemToCart(token, request.itemId(), request.quantity());
        return ResponseEntity.ok(response);
    }
}
```

### 2.4 自动化验证测试套件 (`target/backend/src/test/java/com/modernizer/shop/controller/CartRestControllerTest.java`)
```java
@SpringBootTest
@AutoConfigureMockMvc
class CartRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void testAddItem_SuccessWhenStockSufficient() throws Exception {
        Item mockItem = new Item("item-101", 100.0, 50);
        when(itemRepository.findById("item-101")).thenReturn(Optional.of(mockItem));

        mockMvc.perform(post("/api/v1/cart/items")
                .header("Authorization", "Bearer valid_jwt_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"itemId\":\"item-101\",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.itemCount").value(1));
    }

    @Test
    void testAddItem_RejectsSQLInjectionPayload() throws Exception {
        mockMvc.perform(post("/api/v1/cart/items")
                .header("Authorization", "Bearer valid_jwt_token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"itemId\":\"101 OR 1=1\",\"quantity\":2}"))
                .andExpect(status().isBadRequest());
    }
}
```

---

## 3. 基准用例二：Python 2.7 ➔ Python 3.12+ 异步 ETL 脚本 (`benchmark-python2-scraper`)

### 3.1 业务场景说明
一个 Python 2.7 数据采集脚本，使用 `urllib2` 抓取数据、`xrange` 进行循环控制，并利用阻塞队列进行任务调度。

### 3.2 老旧源源码 (`source/services/metrics_worker.py`)
```python
# -*- coding: utf-8 -*-
import urllib2
import json
import Queue
import time

def fetch_metrics(endpoints):
    results = {}
    for i in xrange(len(endpoints)):
        url = endpoints[i]
        try:
            req = urllib2.Request(url, headers={'User-Agent': 'LegacyCrawler/1.0'})
            response = urllib2.urlopen(req, timeout=5)
            data = json.loads(response.read())
            results[url] = data.get("value", 0)
            print "Fetched %s: %s" % (url, results[url])
        except Exception, e:
            print >> sys.stderr, "Error fetching %s: %s" % (url, str(e))
            results[url] = None
    return results
```

### 3.3 现代化目标源码 (`target/src/services/metrics_worker.py`)
```python
from typing import Dict, List, Optional
import httpx
import asyncio
import logging

logger = logging.getLogger(__name__)

async def fetch_metrics(endpoints: List[str]) -> Dict[str, Optional[float]]:
    results: Dict[str, Optional[float]] = {}
    async with httpx.AsyncClient(timeout=5.0, headers={"User-Agent": "ModernCrawler/2.0"}) as client:
        tasks = [client.get(url) for url in endpoints]
        responses = await asyncio.gather(*tasks, return_exceptions=True)
        
        for url, resp in zip(endpoints, responses):
            if isinstance(resp, Exception):
                logger.error(f"Error fetching {url}: {resp}")
                results[url] = None
            elif isinstance(resp, httpx.Response) and resp.status_code == 200:
                data = resp.json()
                results[url] = data.get("value", 0.0)
                logger.info(f"Fetched {url}: {results[url]}")
            else:
                results[url] = None
    return results
```

### 3.4 自动化验证测试套件 (`target/tests/test_metrics_worker.py`)
```python
import pytest
import respx
import httpx
from target.src.services.metrics_worker import fetch_metrics

@pytest.mark.asyncio
async def test_fetch_metrics_success(respx_mock):
    respx_mock.get("https://api.example.com/metric1").mock(
        return_value=httpx.Response(200, json={"value": 42.5})
    )
    respx_mock.get("https://api.example.com/metric2").mock(
        return_value=httpx.Response(200, json={"value": 18.0})
    )

    urls = ["https://api.example.com/metric1", "https://api.example.com/metric2"]
    result = await fetch_metrics(urls)

    assert result["https://api.example.com/metric1"] == 42.5
    assert result["https://api.example.com/metric2"] == 18.0

@pytest.mark.asyncio
async def test_fetch_metrics_handles_failure(respx_mock):
    respx_mock.get("https://api.example.com/error").mock(
        return_value=httpx.Response(500)
    )
    result = await fetch_metrics(["https://api.example.com/error"])
    assert result["https://api.example.com/error"] is None
```

---

## 4. 基准用例三：Vue 2 Options ➔ Vue 3 Composition API & Pinia (`benchmark-vue2-cart`)

### 4.1 业务场景说明
一个 Vue 2 购物车组件，包含 Options API、`$emit` 向上通知、Vuex 3 Mutation 提交与 Filter 格式化。

### 4.2 老旧源源码 (`source/frontend/src/views/ShoppingCart.vue`)
```vue
<template>
  <div class="cart-container">
    <h2>Shopping Cart ({{ totalCount }})</h2>
    <ul>
      <li v-for="item in items" :key="item.id">
        <span>{{ item.name }} - {{ item.price | currency }}</span>
        <button @click="increment(item)">+</button>
      </li>
    </ul>
    <p>Total: {{ totalPrice | currency }}</p>
  </div>
</template>

<script>
export default {
  name: 'ShoppingCart',
  props: {
    discountRate: { type: Number, default: 1.0 }
  },
  computed: {
    items() {
      return this.$store.state.cart.items;
    },
    totalCount() {
      return this.items.reduce((acc, item) => acc + item.qty, 0);
    },
    totalPrice() {
      const subtotal = this.items.reduce((acc, item) => acc + item.qty * item.price, 0);
      return subtotal * this.discountRate;
    }
  },
  filters: {
    currency(val) {
      return '$' + Number(val).toFixed(2);
    }
  },
  methods: {
    increment(item) {
      this.$store.commit('cart/INCREMENT_ITEM', item.id);
      this.$emit('item-updated', { id: item.id, qty: item.qty + 1 });
    }
  }
};
</script>
```

### 4.3 现代化目标源码 (`target/frontend/src/views/ShoppingCartView.vue`)
```vue
<template>
  <div class="cart-container p-4 bg-zinc-900 rounded-lg text-white">
    <h2 class="text-xl font-bold">Shopping Cart ({{ totalCount }})</h2>
    <ul class="divide-y divide-zinc-800 my-4">
      <li v-for="item in cartStore.items" :key="item.id" class="flex justify-between py-2">
        <span>{{ item.name }} - {{ formatCurrency(item.price) }}</span>
        <button 
          @click="increment(item)"
          class="px-3 py-1 bg-emerald-600 hover:bg-emerald-500 rounded text-sm font-semibold">
          +
        </button>
      </li>
    </ul>
    <p class="font-bold text-lg">Total: {{ formatCurrency(totalPrice) }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useCartStore, type CartItem } from '@/stores/cart';

interface Props {
  discountRate?: number;
}

const props = withDefaults(defineProps<Props>(), {
  discountRate: 1.0
});

const emit = defineEmits<{
  (e: 'item-updated', payload: { id: string; qty: number }): void;
}>();

const cartStore = useCartStore();

const totalCount = computed(() => 
  cartStore.items.reduce((acc, item) => acc + item.qty, 0)
);

const totalPrice = computed(() => {
  const subtotal = cartStore.items.reduce((acc, item) => acc + item.qty * item.price, 0);
  return subtotal * props.discountRate;
});

function formatCurrency(val: number): string {
  return `$${Number(val).toFixed(2)}`;
}

function increment(item: CartItem): void {
  cartStore.incrementItem(item.id);
  emit('item-updated', { id: item.id, qty: item.qty + 1 });
}
</script>
```

### 4.4 自动化验证测试套件 (`target/frontend/src/__tests__/ShoppingCartView.spec.ts`)
```typescript
import { mount } from '@vue/test-utils';
import { createPinia, setActivePinia } from 'pinia';
import { describe, it, expect, beforeEach } from 'vitest';
import ShoppingCart from '../views/ShoppingCartView.vue';
import { useCartStore } from '@/stores/cart';

describe('ShoppingCartView.vue Modernized Component', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('calculates totalCount and discounted totalPrice correctly', () => {
    const store = useCartStore();
    store.items = [
      { id: '1', name: 'Item A', qty: 2, price: 10.0 },
      { id: '2', name: 'Item B', qty: 1, price: 20.0 }
    ];

    const wrapper = mount(ShoppingCart, {
      props: { discountRate: 0.9 }
    });

    expect(wrapper.text()).toContain('Shopping Cart (3)');
    expect(wrapper.text()).toContain('Total: $36.00');
  });

  it('emits item-updated event on increment button click', async () => {
    const store = useCartStore();
    store.items = [{ id: '1', name: 'Item A', qty: 2, price: 10.0 }];

    const wrapper = mount(ShoppingCart);
    await wrapper.find('button').trigger('click');

    expect(wrapper.emitted('item-updated')).toHaveLength(1);
    expect(wrapper.emitted('item-updated')![0]).toEqual([{ id: '1', qty: 3 }]);
  });
});
```

---

## 5. 基准用例四：Node CommonJS ➔ 原生 ESM & Fastify (`benchmark-node-order-pipeline`)

### 5.1 业务场景说明
一个订单校验与签名处理服务，使用 CommonJS 规范编写，包含回调金字塔嵌套（`fs.readFile`, `crypto.pbkdf2`）与错误优先处理。

### 5.2 老旧源源码 (`source/services/orderProcessor.js`)
```javascript
const fs = require('fs');
const crypto = require('crypto');

function processOrder(orderPath, secretKey, callback) {
    fs.readFile(orderPath, 'utf8', function(err, content) {
        if (err) return callback(err);
        try {
            const order = JSON.parse(content);
            if (!order.id || !order.amount || order.amount <= 0) {
                return callback(new Error("INVALID_ORDER"));
            }
            crypto.pbkdf2(order.id, secretKey, 1000, 32, 'sha256', function(err, key) {
                if (err) return callback(err);
                order.signature = key.toString('hex');
                order.processedAt = new Date().toISOString();
                callback(null, order);
            });
        } catch (parseErr) {
            callback(parseErr);
        }
    });
}

module.exports = { processOrder };
```

### 5.3 现代化目标源码 (`target/src/services/orderProcessor.ts`)
```typescript
import { readFile } from 'node:fs/promises';
import { pbkdf2 } from 'node:crypto';
import { promisify } from 'node:util';

const pbkdf2Async = promisify(pbkdf2);

export interface RawOrder {
  id: string;
  amount: number;
  customer: string;
}

export interface ProcessedOrder extends RawOrder {
  signature: string;
  processedAt: string;
}

export async function processOrder(orderPath: string, secretKey: string): Promise<ProcessedOrder> {
  const content = await readFile(orderPath, 'utf-8');
  const order: RawOrder = JSON.parse(content);

  if (!order.id || typeof order.amount !== 'number' || order.amount <= 0) {
    throw new Error('INVALID_ORDER');
  }

  const derivedKey = await pbkdf2Async(order.id, secretKey, 1000, 32, 'sha256');
  
  return {
    ...order,
    signature: derivedKey.toString('hex'),
    processedAt: new Date().toISOString()
  };
}
```

### 5.4 自动化验证测试套件 (`target/tests/orderProcessor.test.ts`)
```typescript
import { describe, it, expect, vi } from 'vitest';
import { processOrder } from '../src/services/orderProcessor.js';

vi.mock('node:fs/promises', () => ({
  readFile: vi.fn().mockImplementation(async (path: string) => {
    if (path.includes('valid')) {
      return JSON.stringify({ id: 'ORD-999', amount: 150.0, customer: 'Alice' });
    }
    if (path.includes('invalid')) {
      return JSON.stringify({ id: 'ORD-000', amount: -10.0 });
    }
    throw new Error('FILE_NOT_FOUND');
  })
}));

describe('Node 24 ESM Order Processor', () => {
  it('processes valid order and signs hash successfully', async () => {
    const result = await processOrder('valid.json', 'test-secret');
    expect(result.id).toBe('ORD-999');
    expect(result.amount).toBe(150.0);
    expect(result.signature).toBeDefined();
    expect(result.processedAt).toBeTypeOf('string');
  });

  it('rejects invalid order with negative amount', async () => {
    await expect(processOrder('invalid.json', 'test-secret'))
      .rejects.toThrow('INVALID_ORDER');
  });
});
```

---

## 6. 基准用例五：Spring Boot 2.7 & Vue 2.7 ➔ Spring Boot 3.4 & Vue 3.5 旗舰级全栈靶场 (`benchmark-springboot2-vue2-payment`)

### 6.1 业务场景说明
作为系统的旗舰级核心全栈靶场，模拟银行与第三方清结算系统的高频资金往来（Money-Critical）场景：
1. **订单支付与余额扣减**：基于 Redis 分布式锁（Jedis SETNX + Lua 原子解锁）防止高并发超卖；账户钱包基于 JPA `@Version` 乐观锁 CAS（`WHERE version = ? AND balance >= ?`）扣款；
2. **防重幂等与滑动窗口限流**：针对收银台高频扣款，集成 Redis 令牌桶滑动窗口限流（5 req/s）与 `X-Idempotency-Token` 幂等防重；
3. **退款状态机流转与防呆保护**：支持多次部分退款（Partial Refund）与全额退款，严格执行退款上限防呆校验（已退金额累计不得超过订单支付总额）；
4. **财务审计流水快照**：记账流水显式沉淀 `before_balance`、`delta_amount` 与 `after_balance`，确保双向账实相符；
5. **行为等价性测试**：内置工业级全流程集成测试套件 `PaymentServiceIntegrationTest.java`。

### 6.2 老旧源源码 (`source/backend/src/main/java/com/enterprise/pay/service/impl/PaymentServiceImpl.java`)
```java
package com.enterprise.pay.service.impl;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private JedisPool jedisPool;

    @Transactional
    public OrderResponseDTO payOrder(PaymentRequestDTO request) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 1. 手动获取分布式锁 (SETNX 坏味道)
            String lockKey = "lock:order:" + request.getOrderNo();
            String lockResult = jedis.set(lockKey, "LOCKED", "NX", "EX", 10);
            if (!"OK".equals(lockResult)) {
                throw new IllegalStateException("System busy, please retry");
            }
            
            // 2. 幂等校验与扣减业务逻辑 (javax 事务)
            // ...
        } finally {
            if (jedis != null) {
                // 手写 Lua 释放锁并归还连接池
                jedis.eval("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end", 1, lockKey, "LOCKED");
                jedis.close();
            }
        }
    }
}
```

### 6.3 现代化目标源码 (`target/backend/src/main/java/com/enterprise/pay/service/impl/PaymentServiceImpl.java`)
```java
package com.enterprise.pay.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.enterprise.pay.dto.PaymentRequestRecord;
import com.enterprise.pay.dto.OrderResponseRecord;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final StringRedisTemplate redisTemplate;
    private final DistributedLockTemplate lockTemplate;

    public PaymentServiceImpl(StringRedisTemplate redisTemplate, DistributedLockTemplate lockTemplate) {
        this.redisTemplate = redisTemplate;
        this.lockTemplate = lockTemplate;
    }

    @Transactional
    public OrderResponseRecord payOrder(PaymentRequestRecord request) {
        String lockKey = "lock:order:" + request.orderNo();
        return lockTemplate.executeWithLock(lockKey, Duration.ofSeconds(10), () -> {
            // 优雅现代化声明式执行、Record DTO 不可变保障与 Jakarta 持久化
            return executePaymentLogic(request);
        });
    }
}
```

### 6.4 自动化验证测试套件 (`target/backend/src/test/java/com/enterprise/pay/PaymentServiceIntegrationTest.java`)
```java
@SpringBootTest
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private WalletService walletService;

    @Test
    public void testCompletePaymentAndRefundLifecycle() {
        // 1. 验证创建订单与钱包扣款一致性
        OrderEntity order = paymentService.createOrder(8899L, "MacBook Pro Kit", new BigDecimal("150.00"));
        PaymentRequestDTO payReq = new PaymentRequestDTO(order.getOrderNo(), 8899L, new BigDecimal("150.00"), PaymentChannel.WALLET_BALANCE);
        OrderResponseDTO paidOrder = paymentService.payOrder(payReq);
        Assertions.assertEquals(OrderStatus.SUCCESS, paidOrder.getStatus());

        // 2. 验证幂等防重拦截 (重复提交必抛出异常)
        Assertions.assertThrows(IllegalStateException.class, () -> paymentService.payOrder(payReq));

        // 3. 验证部分退款与累计超限防呆保护
        RefundRecordEntity partial = refundService.applyRefund(new RefundRequestDTO(order.getOrderNo(), 8899L, new BigDecimal("50.00")));
        refundService.auditRefund(partial.getRefundNo(), true, "Auditor");
        
        // 尝试退款超额（总计 150，已退 50，再退 110 必须被防呆拦截）
        Assertions.assertThrows(IllegalStateException.class, () -> 
            refundService.applyRefund(new RefundRequestDTO(order.getOrderNo(), 8899L, new BigDecimal("110.00")))
        );
    }
}
```

---

## 7. 端到端量化验收标准矩阵

| 基准测试套件 | 靶场核心业务场景 | 源生态 | 目标生态 | 目标 $P_{\text{tests}}$ | 目标 $C_{\text{ast}}$ | 目标 $M_{\text{schema}}$ | 准入综合评分 $S_{\text{fidelity}}$ |
| :--- | :--- | :--- | :--- | :---: | :---: | :---: | :---: |
| `benchmark-jsp-ecommerce` | 电商购物车与阶梯满减结算 | JSP, Struts, JDBC | Spring Boot 3, Java 21, JPA | $\ge 95\%$ | $\ge 90\%$ | $100\%$ | **$\ge 94.5\%$** |
| `benchmark-python2-scraper` | 金融外汇折算与风险批处理 | Python 2.7, `urllib2` | Python 3.12+, `httpx`, Async | $\ge 98\%$ | $\ge 95\%$ | $100\%$ | **$\ge 97.5\%$** |
| `benchmark-vue2-cart` | 信贷敞口评估与总账台账 | Vue 2 Options, Vuex | Vue 3 `<script setup>`, Pinia | $\ge 98\%$ | $\ge 95\%$ | $100\%$ | **$\ge 97.5\%$** |
| `benchmark-node-order-pipeline` | 跨境支付与银行流水对账网关 | Node CJS, Callbacks | Node 24 ESM, Async/Await | $100\%$ | $100\%$ | $100\%$ | **$100.0\%$** |
| `benchmark-springboot2-vue2-payment`<br>*(旗帜级全栈靶场)* | **核心支付收银台、Redis 分布式锁、余额乐观锁 CAS、退款状态机** | **Spring Boot 2.7, javax.*, Jedis, Vue 2.7** | **Spring Boot 3.4, Java 21 Record, Jakarta, Vue 3.5, Pinia** | **$100\%$** | **$\ge 98\%$** | **$100\%$** | **$\ge 98.8\%$** |

