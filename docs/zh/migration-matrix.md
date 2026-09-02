# 🗺️ 现代化赛道与转换矩阵 (Modernization Tracks & Migration Matrix)

<p align="center">
  <a href="../migration-matrix.md">English Version</a> | <a href="./migration-matrix.md">简体中文版</a>
</p>

---

## 1. 赛道一：JSP ➔ Java 21 / Spring Boot 3 & 现代 Web

```mermaid
flowchart TD
    subgraph LegacyJSP ["老旧源工程资产 (Legacy Source)"]
        J1["JSP 模板文件: 脚本片段与 JSTL 标签"]
        J2["老旧 Servlet / Struts Action 控制器"]
        J3["HttpSession 状态与原生 JDBC 拼接语句"]
    end

    subgraph TransformationEngine ["AST 符号提取与重构转换引擎"]
        P1["表单标签转现代前端组件与 DTO 契约"]
        P2["Servlet 路由映射至 Spring Controller 与 Service"]
        P3["Session 机制转无状态 JWT 与 Spring Data JPA"]
    end

    subgraph ModernStack ["现代化目标工程 (Java 21 + Spring Boot 3)"]
        M1["Spring Boot RestController 与 DTO Record"]
        M2["Spring Data JPA Repository 与 Domain 实体"]
        M3["现代前端组件: Vue 3 / React 或 Thymeleaf 模板"]
    end

    J1 --> P1 --> M1
    J2 --> P2 --> M2
    J3 --> P3 --> M3
```

### 核心转换规则与业务保真守卫
- **脚本解耦（Scriptlet Decoupling）**：严格提取 `<% ... %>` 中的业务逻辑，封装为高内聚的 Spring `@Service` 业务方法。
- **表单对象绑定**：将 `<form action="...">` 与 `<jsp:useBean>` 映射为 Java 21 `record` 强类型 DTO，自动注入 `@Valid` 与 Jakarta 校验注解。
- **SQL 注入防范**：将字符串拼接的 `Statement.executeQuery("SELECT ... " + id)` 彻底重构为 Spring Data JPA 参数化查询或 ORM 方法。

---

## 2. 赛道二：Python 2.7 ➔ Python 3.12+ & FastAPI 异步化

```mermaid
flowchart TD
    subgraph Py2Source ["老旧 Python 2.7 工程"]
        P1["无括号的 print 语句与 exec 语法"]
        P2["urllib / urllib2 / httplib 废弃导入"]
        P3["str 与 unicode 混淆及 xrange 遍历"]
        P4["无类型注解的同步阻塞 Web 接口"]
    end

    subgraph Transformer ["Python AST 转换与类型生成器"]
        T1["AST 语法树调用与括号格式化"]
        T2["现代标准库与第三方库平滑替换"]
        T3["严格的 UTF-8 编码与 Byte/String 分离"]
        T4["类型注解 (Typing) 注入与 Asyncio 异步化包装"]
    end

    subgraph Py3Target ["现代化 Python 3.12+ 目标栈"]
        R1["带格式化参数的标准 print 函数"]
        R2["urllib.request / httpx 现代网络库"]
        R3["原生的 str 字符串与 bytes 严格类型约束"]
        R4["FastAPI 异步路由处理器与 Pydantic 校验模型"]
    end

    P1 --> T1 --> R1
    P2 --> T2 --> R2
    P3 --> T3 --> R3
    P4 --> T4 --> R4
```

### 核心转换规则与业务保真守卫
- **语法升级**：`print "text"` ➔ `print("text")`，`except Exception, e` ➔ `except Exception as e`。
- **标准库与依赖升级**：`urllib2` ➔ `httpx` / `urllib.request`；`Queue` ➔ `queue`；`ConfigParser` ➔ `configparser`；清除 `six` 过渡依赖。
- **字符串与字节流安全**：在文件操作与网络 Socket 交互处严格补齐 `encode('utf-8')` 与 `decode('utf-8')`，杜绝 Python 3 隐式 `TypeError`。

---

## 3. 赛道三：Vue 2 / React Class / jQuery ➔ Vue 3 / React 19 + TypeScript

```mermaid
flowchart TD
    subgraph LegacyFrontend ["老旧前端代码资产"]
        V1["Vue 2 new Vue(data, methods, computed)"]
        V2["全局 EventBus 与 Vuex 3 状态"]
        V3["jQuery 原生 DOM 强操作"]
    end

    subgraph ASTConverter ["Vue SFC / TypeScript 编译转换器"]
        C1["Options API 转换为 script setup lang=ts"]
        C2["EventBus 模式平滑迁移至 Pinia 状态库"]
        C3["命令式 DOM 修改转为声明式响应式变量 (ref/computed)"]
    end

    subgraph ModernFrontend ["现代化前端工程 (Vue 3 / React 19)"]
        T1["Vue 3 script setup / React 19 Hooks 函数式组件"]
        T2["Pinia 类型安全 Store (State, Getters, Actions)"]
        T3["Tailwind CSS 原子化样式与声明式数据绑定"]
    end

    V1 --> C1 --> T1
    V2 --> C2 --> T2
    V3 --> C3 --> T3
```

### 核心转换规则与业务保真守卫
- **响应式重构**：将 `this.someData` 映射为 `ref()` / `reactive()`，并声明严格的 TypeScript Interface。
- **生命周期映射**：`beforeDestroy` ➔ `onBeforeUnmount`，`destroyed` ➔ `onUnmounted`，`mounted` ➔ `onMounted`。
- **插槽语法规范化**：将废弃的 `slot="header"` 与 `slot-scope="props"` 升级为现代 `#header="props"`。

---

## 4. 赛道四：Node CommonJS / 回调地狱 ➔ 原生 ESM & Node.js 24 LTS

```mermaid
flowchart TD
    subgraph CJSCode ["老旧 Node.js CommonJS 模块"]
        N1["const pkg = require('./pkg')"]
        N2["module.exports = ..."]
        N3["fs.readFile(path, callback) 回调嵌套"]
        N4["老旧 Express 错误优先回调中间件"]
    end

    subgraph NodeAST ["Node.js 24 现代重构编译器"]
        A1["静态 Import 解析与显式扩展名补齐"]
        A2["Export Default 与命名导出精准转换"]
        A3["Promise 与 fs/promises 异步流水线重构"]
        A4["Fastify / NestJS 原生异步插件与控制器架构"]
    end

    subgraph ESMCode ["现代化 Node.js 24 原生 ESM 目标"]
        E1["import pkg from './pkg.js'"]
        E2["export default ... / export const ..."]
        E3["const data = await fs.readFile(path)"]
        E4["Fastify 现代化原生异步路由处理器"]
    end

    N1 --> A1 --> E1
    N2 --> A2 --> E2
    N3 --> A3 --> E3
    N4 --> A4 --> E4
```

### 核心转换规则与业务保真守卫
- **显式扩展名补齐**：Node.js 官方原生 ESM 规范要求相对路径导入必须附带 `.js` 或 `.ts` 扩展名。
- **顶级 Await 利用**：合理利用 Node 24 原生 Top-Level Await 简化数据库与配置的初始化启动流程。
- **消除回调嵌套**：使用 `node:fs/promises`、`node:stream/promises` 与 `node:util.promisify` 将回调金字塔彻底扁平化为 `async/await`。
