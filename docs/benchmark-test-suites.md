# 🧪 Benchmark Test Suites & Quantifiable Preservation Metrics

This document defines the ground-truth benchmark datasets, exact legacy code inputs, target modernized code schemas, synthesized test suites, and the mathematical formula for computing the **Business Logic Preservation Score**.

---

## 1. Mathematical Preservation Metric Formula

To eliminate subjective evaluations, the **Test & Quality Verifier Agent** calculates a deterministic, composite **Business Logic Preservation Score** ($S_{\text{fidelity}} \in [0, 100\%]$):

$$S_{\text{fidelity}} = 0.50 \times P_{\text{tests}} + 0.30 \times C_{\text{ast}} + 0.20 \times M_{\text{schema}}$$

Where:
- **$P_{\text{tests}}$ (Dynamic Test Pass Rate)**:
  $$P_{\text{tests}} = \frac{N_{\text{passed}}}{N_{\text{total\_tests}}} \times 100\%$$
  Measures the percentage of synthesized unit and regression assertions passing in the sandboxed runtime (JUnit 5, PyTest, Vitest).
- **$C_{\text{ast}}$ (Public AST Symbol Coverage)**:
  $$C_{\text{ast}} = \frac{|\text{Symbols}_{\text{target}} \cap \text{Symbols}_{\text{source\_mapped}}|}{|\text{Symbols}_{\text{source\_public}}|} \times 100\%$$
  Measures whether all public endpoints, exported methods, DTO properties, and state mutations in the legacy system are preserved in the modernized AST.
- **$M_{\text{schema}}$ (API Contract & Schema Conformance)**:
  $$M_{\text{schema}} = \frac{N_{\text{matching\_fields}}}{N_{\text{total\_contract\_fields}}} \times 100\%$$
  Measures JSON/HTTP payload schema equivalence between legacy input/output contracts and modernized REST/GraphQL contracts.

---

## 2. Benchmark Suite 1: JSP ➔ Spring Boot 3 & Modern REST (`benchmark-jsp-ecommerce`)

### 2.1 Business Scenario
A legacy e-commerce session-based authentication and cart management module containing JSP form tags, scriptlet Java logic, raw `HttpSession` manipulation, and concatenated JDBC database access.

### 2.2 Legacy Source Code (`source/backend/src/main/webapp/cart.jsp`)
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

### 2.3 Modernized Target Code (`target/backend/src/main/java/com/modernizer/shop/controller/CartRestController.java`)
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

### 2.4 Synthesized Verification Test Suite (`target/backend/src/test/java/com/modernizer/shop/controller/CartRestControllerTest.java`)
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
                .content("{\"itemId\":\"1 OR 1=1\",\"quantity\":1}"))
                .andExpect(status().isBadRequest());
    }
}
```

---

## 3. Benchmark Suite 2: Python 2.7 ➔ Python 3.12+ Async ETL Worker (`benchmark-python2-scraper`)

### 3.1 Business Scenario
A Python 2.7 data ETL script fetching metrics using `urllib2`, parsing text with `xrange`, and dispatching jobs over a thread-safe `Queue.Queue`.

### 3.2 Legacy Source Code (`source/services/metrics_worker.py`)
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

### 3.3 Modernized Target Code (`target/src/services/metrics_worker.py`)
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

### 3.4 Synthesized Verification Test Suite (`target/tests/test_metrics_worker.py`)
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

## 4. Benchmark Suite 3: Vue 2 Options ➔ Vue 3 Composition API & Pinia (`benchmark-vue2-cart`)

### 4.1 Business Scenario
A Vue 2 shopping cart component using Options API, `$emit`, legacy Vuex mutations, and filters.

### 4.2 Legacy Source Code (`source/frontend/src/views/ShoppingCart.vue`)
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

### 4.3 Modernized Target Code (`target/frontend/src/views/ShoppingCartView.vue`)
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

### 4.4 Synthesized Verification Test Suite (`target/frontend/src/__tests__/ShoppingCartView.spec.ts`)
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

## 5. Benchmark Suite 4: Node CommonJS ➔ Node.js 24 ESM Fastify Plugin (`benchmark-node-order-pipeline`)

### 5.1 Business Scenario
An order validation and processing service written in CommonJS using callback pyramids (`fs.readFile`, `crypto.pbkdf2`) and legacy Express middlewares.

### 5.2 Legacy Source Code (`source/services/orderProcessor.js`)
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

### 5.3 Modernized Target Code (`target/src/services/orderProcessor.ts`)
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

### 5.4 Synthesized Verification Test Suite (`target/tests/orderProcessor.test.ts`)
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

## 6. End-to-End Evaluation Criteria Matrix

| Benchmark Suite | Source Ecosystem | Target Ecosystem | Target $P_{\text{tests}}$ | Target $C_{\text{ast}}$ | Target $M_{\text{schema}}$ | Required Minimum $S_{\text{fidelity}}$ |
| :--- | :--- | :--- | :---: | :---: | :---: | :---: |
| `benchmark-jsp-ecommerce` | JSP, Struts, JDBC | Spring Boot 3, Java 21, JPA | $\ge 95\%$ | $\ge 90\%$ | $100\%$ | **$\ge 94.5\%$** |
| `benchmark-python2-scraper` | Python 2.7, `urllib2` | Python 3.12+, `httpx`, Async | $\ge 98\%$ | $\ge 95\%$ | $100\%$ | **$\ge 97.5\%$** |
| `benchmark-vue2-cart` | Vue 2 Options, Vuex | Vue 3 `<script setup>`, Pinia | $\ge 98\%$ | $\ge 95\%$ | $100\%$ | **$\ge 97.5\%$** |
| `benchmark-node-order-pipeline` | Node CJS, Callbacks | Node 24 ESM, Async/Await | $100\%$ | $100\%$ | $100\%$ | **$100.0\%$** |
