# 🗺️ Modernization Tracks & Transformation Matrix

<p align="center">
  <a href="./migration-matrix.md">English Version</a> | <a href="./zh/migration-matrix.md">简体中文版</a>
</p>

---

## 1. Track 1: JSP ➔ Java / Spring Boot 3 & Modern Web

```mermaid
flowchart TD
    subgraph LegacyJSP ["Legacy Source Artifacts"]
        J1["JSP File with Scriptlets & JSTL Tags"]
        J2["Legacy Servlet / Struts Action"]
        J3["Http Session & Raw JDBC Statements"]
    end

    subgraph TransformationEngine ["AST & Symbol Refactoring Engine"]
        P1["Tag to Component & DTO Extractor"]
        P2["Servlet to Spring Controller & Service Mapper"]
        P3["Session to Stateless JWT & Spring Data JPA"]
    end

    subgraph ModernStack ["Modern Target Artifacts (Java 21 + Spring Boot 3)"]
        M1["Spring Boot RestController / DTO Record"]
        M2["Spring Data JPA Repository & Domain Model"]
        M3["Modern Component: Vue 3 / React or Thymeleaf"]
    end

    J1 --> P1 --> M1
    J2 --> P2 --> M2
    J3 --> P3 --> M3
```

### Key Migration Rules
- **Scriptlet Decoupling**: Extract `<% ... %>` Java code into dedicated Spring `@Service` methods.
- **Form Bindings**: Convert `<form action="...">` and `<jsp:useBean>` into Java 21 `record` DTO classes with `@Valid` and Jakarta constraints.
- **SQL Sanitization**: Replace raw concatenated `Statement.executeQuery("SELECT ... " + var)` with Spring Data JPA parameterized queries or query methods.

---

## 2. Track 2: Python 2.x ➔ Python 3.12+ & FastAPI

```mermaid
flowchart TD
    subgraph Py2Source ["Legacy Python 2 Codebase"]
        P1["print Statements without Parentheses"]
        P2["urllib / urllib2 / httplib Imports"]
        P3["str / unicode Ambiguities & xrange"]
        P4["Untyped Synchronous Flask/WSGI Endpoints"]
    end

    subgraph Transformer ["Python AST & Type Synthesizer"]
        T1["AST Function Call Restructuring"]
        T2["Modern Standard Library Substitution"]
        T3["Bytes/String Strict Encoding & Range Converters"]
        T4["Type Annotation Injection & Asyncio Wrapping"]
    end

    subgraph Py3Target ["Modern Python 3.12+ Target"]
        R1["print Function with Format Specifiers"]
        R2["urllib.request / httpx Modern APIs"]
        R3["Native utf-8 str / bytes Strict Typing"]
        R4["FastAPI Async Handlers with Pydantic Models"]
    end

    P1 --> T1 --> R1
    P2 --> T2 --> R2
    P3 --> T3 --> R3
    P4 --> T4 --> R4
```

### Key Migration Rules
- **Print / Exec**: Transform statements into function calls (`print("msg")`).
- **Standard Library Realignment**: `urllib2` ➔ `urllib.request` or modern `httpx`; `Queue` ➔ `queue`; `ConfigParser` ➔ `configparser`.
- **String & Byte Fidelity**: Explicitly encode/decode when interfacing with I/O sockets or files to prevent silent Python 3 `TypeError` regressions.

---

## 3. Track 3: Vue 2 / React Class / jQuery ➔ Vue 3 / React 19 + TypeScript

```mermaid
flowchart TD
    subgraph LegacyFrontend ["Legacy Frontend Components"]
        V1["Vue 2 new Vue(data, methods, computed)"]
        V2["Global EventBus and Vuex 3"]
        V3["jQuery DOM Manipulation"]
    end

    subgraph ASTConverter ["Vue / TS Compiler Engine"]
        C1["Options to Composition API script setup lang=ts"]
        C2["EventBus to Mitt / Pinia Reactive Stores"]
        C3["Imperative DOM to Declarative Reactivity (ref/computed)"]
    end

    subgraph ModernFrontend ["Modern Frontend Target"]
        T1["Vue 3 script setup / React 19 Function Components"]
        T2["Pinia Type-Safe Stores with Actions & State"]
        T3["Tailwind CSS Utility Classes & Declarative Bindings"]
    end

    V1 --> C1 --> T1
    V2 --> C2 --> T2
    V3 --> C3 --> T3
```

### Key Migration Rules
- **Reactivity Migration**: Convert `this.property` into `ref()` / `reactive()` with explicit TypeScript interfaces.
- **Lifecycle Mapping**: `beforeDestroy` ➔ `onBeforeUnmount`, `destroyed` ➔ `onUnmounted`, `mounted` ➔ `onMounted`.
- **Slot Syntax**: Convert deprecated `slot="header"` and `slot-scope="props"` to `#header="props"`.

---

## 4. Track 4: Node CommonJS / Callbacks ➔ Native ESM & Node.js 24 LTS

```mermaid
flowchart TD
    subgraph CJSCode ["Legacy Node.js CommonJS"]
        N1["const pkg = require('./pkg')"]
        N2["module.exports = ..."]
        N3["fs.readFile callback pyramid"]
        N4["Legacy Express Error-First Callbacks"]
    end

    subgraph NodeAST ["Node.js 24 Refactoring Transformer"]
        A1["Static Import Parser & Extension Resolver"]
        A2["Export Default / Named Synthesizer"]
        A3["Promise & fs/promises Async/Await Modernizer"]
        A4["Fastify Native Plugin & Route Architecture"]
    end

    subgraph ESMCode ["Modern Node.js 24 Native ESM"]
        E1["import pkg from './pkg.js'"]
        E2["export default ... / export const ..."]
        E3["const data = await fs.readFile(path)"]
        E4["Fastify Native Async Controller"]
    end

    N1 --> A1 --> E1
    N2 --> A2 --> E2
    N3 --> A3 --> E3
    N4 --> A4 --> E4
```

### Key Migration Rules
- **Import Specifiers**: Append explicit `.js` or `.ts` file extensions required by Node.js native ESM standard.
- **Top-Level Await**: Leverage Node.js 24 top-level await for database connections and configuration bootstrapping.
- **Callback Elimination**: Convert callback-heavy APIs to `node:util.promisify` or native promise namespaces (`node:fs/promises`, `node:stream/promises`).
