# 🚀 Legacy Code Modernizer

<p align="center">
  <strong>Autonomous AI-Powered Legacy System Modernization & Refactoring Workbench</strong><br>
  <em>Preserving Core Business Logic with Zero Disruption across Legacy Ecosystems</em>
</p>

<p align="center">
  <a href="./README.md"><strong>English</strong></a> | <a href="./README_zh.md"><strong>简体中文</strong></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Node.js-24%20LTS-339933?style=flat-square&logo=node.js&logoColor=white" alt="Node.js 24 LTS" />
  <img src="https://img.shields.io/badge/LLM-DeepSeek--v4--pro-4f46e5?style=flat-square" alt="DeepSeek-v4-pro" />
  <img src="https://img.shields.io/badge/Architecture-Tri--Agent%20ReAct-6366f1?style=flat-square" alt="Tri-Agent ReAct" />
  <img src="https://img.shields.io/badge/Frontend-VS%20Code%20IDE%20Style-007ACC?style=flat-square&logo=visualstudiocode&logoColor=white" alt="VS Code Web UI" />
  <img src="https://img.shields.io/badge/AST%20Engine-Tree--Sitter%20%7C%20Babel-f59e0b?style=flat-square" alt="AST Engine" />
  <img src="https://img.shields.io/badge/Verification-Automated%20Regression%20Tests-10b981?style=flat-square" alt="Verification" />
</p>

---

## 🌟 Agent Opening Pitch & Core Mission

> **"Dedicated to enterprise-grade intelligent legacy system modernization. Deeply focusing on four core technical tracks: JSP ➔ Java/Spring Boot ecosystem migration, Python ecosystem upgrade, Vue/React modern stack evolution, and Node.js modernization. Powered by an autonomous Tri-Agent engineering team orchestrated by DeepSeek-v4-pro, AST-level precision transformations, live official documentation retrieval, and regression verification to achieve zero-disruption, whole-repository modernization."**

---

## 💡 Overview & The Problem

Enterprise legacy codebases (e.g., monolithic JSP applications, Python 2 scripts, Vue 2 Options API components, legacy CommonJS callback pyramids) represent core business assets, yet they incur enormous technical debt and security vulnerabilities:
1. **Manual Refactoring is Prohibitive**: Cross-version multi-file migrations take weeks or months of senior developer effort.
2. **Implicit Business Logic Fragility**: Undocumented edge cases and implicit dependencies often break during manual rewrites.
3. **Naive LLM Limitations**: Simple single-pass prompting lacks global dependency awareness, frequently hallucinates deprecated APIs, and fails to handle whole-repository context.

**Legacy Code Modernizer** solves these challenges by combining **Deterministic AST Tools**, **Autonomous ReAct Agents powered by DeepSeek-v4-pro (inspired by Claude Code and grok-build)**, and **Regression Testing Sandboxes** within an intuitive VS Code-inspired Web IDE.

---

## 🎯 Supported Modernization Tracks

```mermaid
mindmap
  root((Legacy Code Modernizer))
    JSP to Java / Spring Boot
      JSP Tags to Modern DTOs & REST APIs
      Struts/Servlets to Spring Boot 3.x
      Session Management to Stateless JWT / Security
      Java 8 to Java 21 LTS
    Python Ecosystem
      Python 2.7 to Python 3.12+
      Legacy Flask/Django to FastAPI/Modern Django
      Type Annotations & Modern Asyncio
      Six & urllib2 Deprecation Purge
    Vue / React Ecosystem
      Vue 2 Options API to Vue 3 Script Setup & TS
      React Legacy Class Components to React 19 Hooks
      jQuery DOM Manipulation to Declarative State
      Vuex to Pinia / Redux to Zustand
    Node.js Ecosystem
      CommonJS require to Native ESM import
      Callback Pyramids to Async / Await
      Express Legacy to Fastify / NestJS
      Engine Upgrade to Node.js 24 LTS
```

| Modernization Track | Legacy Source Stack | Target Modern Stack | Core Transformation & Business Guardrails |
| :--- | :--- | :--- | :--- |
| **JSP ➔ Java/Spring Boot** | JSP Custom Tags, Struts, Servlets, Java 8 | Spring Boot 3.x, REST APIs, Modern Web Frontend, Java 21 | Form tags to DTO bindings, Session to Token auth, SQL injection cleanup |
| **Python Ecosystem** | Python 2.7, Old Flask/Django, `urllib2`, `six` | Python 3.12+, FastAPI, Type Hints (`typing`), `asyncio` | String/bytes stream refactoring, deprecated standard lib substitutions |
| **Vue / React Ecosystem** | Vue 2 Options API, React Class Components, jQuery | Vue 3 `<script setup>`, React 19 Hooks + TypeScript + Tailwind | Reactive state mappings, lifecycle hooks translation, clean composables |
| **Node.js Ecosystem** | CommonJS (`require`), Callback Hell, Express 3/4 | Native ESM (`import`), Async/Await, Fastify, Node 24 LTS | Static module resolution, Promise orchestration, memory leak diagnostics |

---

## 🏛️ System Architecture

```mermaid
graph TD
    subgraph Client ["User Interface Layer (VS Code-Style Web Workbench)"]
        A1["1-Click Sandbox Demos"] --> UI["Workbench Workspace"]
        A2["Local ZIP Upload"] --> UI
        A3["GitHub URL + Token"] --> UI
        UI --> Diff["Monaco Side-by-Side Diff Editor"]
        UI --> Term["xterm.js Live Agent Terminal"]
        UI --> Topo["XYFlow Business Topology View"]
        UI --> QA["Verification & QA Dashboard"]
    end

    subgraph Communication ["Bi-Directional Communication Layer"]
        UI <-->|RESTful APIs| HTTP["API Gateway"]
        UI <-->|Server-Sent Events / SSE| Stream["Realtime Event Streamer"]
    end

    subgraph Engine ["Node.js 24 LTS Backend Agent Runtime"]
        HTTP --> Orch["Tri-Agent Orchestrator"]
        Stream <--> Orch

        subgraph Agents ["Autonomous Agent Matrix"]
            Ag1["🧠 Modernize Architect Agent<br/>(Dependency & Domain Topology)"]
            Ag2["🛠️ Code Transformer Agent<br/>(AST Patching & Code Migration)"]
            Ag3["🧪 Test & Quality Verifier Agent<br/>(Regression Testing & Business Scoring)"]
        end

        Orch --> Ag1
        Orch --> Ag2
        Orch --> Ag3

        subgraph LLM_Layer ["LLM Inference & Cache Layer"]
            DS["DeepSeek-v4-pro Engine"]
            Cache["Prompt Caching (Prefix Lock)"]
            Cache --> DS
        end

        Ag1 <--> LLM_Layer
        Ag2 <--> LLM_Layer
        Ag3 <--> LLM_Layer

        subgraph Skills ["Built-in Skill Suite"]
            Sk1["💬 grill-me Decision Skill<br/>(Architectural Decision Interviews)"]
            Sk2["🗺️ Codebase Domain Modeler<br/>(End-to-End Business Flow Analysis)"]
            Sk3["🔍 Live Doc Searcher<br/>(Web Crawling Official Migration Guides)"]
        end

        Ag1 -.-> Sk1
        Ag1 -.-> Sk2
        Ag2 -.-> Sk3

        subgraph Toolbox ["Deterministic Toolset"]
            T1["search_symbols_and_deps"]
            T2["read_source_slice"]
            T3["apply_ast_patch"]
            T4["verify_syntax_and_types"]
            T5["run_regression_tests"]
        end

        Ag1 --> T1
        Ag2 --> T2
        Ag2 --> T3
        Ag2 --> T4
        Ag3 --> T5
    end

    subgraph Deliverables ["Output Pipeline"]
        Orch --> PR["GitHub Pull Request with Changelog"]
        Orch --> ZIP["Modernized Source ZIP Package"]
    end
```

---

## 🤖 Tri-Agent Engineering Team & Built-in Skills

```mermaid
sequenceDiagram
    autonumber
    actor User as "User / Developer"
    participant Arch as "🧠 Architect Agent"
    participant Trans as "🛠️ Transformer Agent"
    participant Test as "🧪 QA & Test Agent"
    participant Tools as "🧰 AST & Tool Sandbox"

    User->>Arch: Ingest Legacy Repository (Demo/ZIP/Git)
    Arch->>Tools: Scan symbols, build AST dependency graph
    Tools-->>Arch: Return topological sort & business flow

    opt Ambiguous Architectural Direction
        Arch->>User: Trigger "grill-me" interactive interview
        User-->>Arch: Provide architectural preference (e.g. REST vs Thymeleaf)
    end

    Arch->>Trans: Dispatch Step-by-Step Migration Roadmap
    loop For each component (Bottom-Up)
        Trans->>Tools: Read source slices & inspect types
        opt Niche or Breaking APIs
            Trans->>Tools: Live web search for official migration docs
        end
        Trans->>Tools: Apply AST-guided modern code patch (via DeepSeek-v4-pro)
        Trans->>Tools: Run static syntax & type check
        alt Syntax / Type Error
            Trans->>Trans: Self-Reflection & Correction Loop
        end
    end

    Trans->>Test: Hand over modernized codebase
    Test->>Tools: Synthesize unit/regression test cases
    Test->>Tools: Execute test suites in sandbox
    Tools-->>Test: Test execution results
    Test-->>User: Stream Business Logic Preservation Score (e.g. 99.4%)
    Test->>User: Export Modernized ZIP / Submit GitHub PR
```

1. **🧠 Modernize Architect Agent**: Performs static analysis, resolves cross-file import dependencies, orders files topologically (bottom-up), and maps end-to-end business flows. Houses the built-in **`grill-me` skill** to interview users on critical architectural branches. (DeepSeek-v4-pro Reasoning Mode).
2. **🛠️ Code Transformer Agent**: Executes surgical code modifications file-by-file. Armed with **`Live Web Search`** to query official documentation on breaking changes, backed by an **AST Self-Reflection Loop** to fix compiler/syntax errors immediately. (DeepSeek-v4-pro Temperature 0.0).
3. **🧪 Test & Quality Verifier Agent**: Generates comprehensive modern test suites (Vitest, JUnit 5, PyTest) reflecting historical assertions, executing tests in sandboxes to measure and certify the **Business Logic Preservation Score**.

---

## 🖥️ VS Code-Style Developer Workbench

Crafted according to anti-slop, developer-first engineering aesthetics:

```
+---+------------------+-----------------------------------------------+--------------------+
| A | Sidebar          | Editor Area                                   | Secondary Sidebar  |
| C | • Dual File Tree | • Monaco Side-by-Side Diff View               | (Agent Command)    |
| T |   (Legacy vs Mod)| • Line-by-Line AI Rationale Annotations       | • Tri-Agent Chat   |
| I | • Business Flow  | • Multi-Tab Code Editing                      | • Live Tool Calls  |
| V |   Topology Map   | • Realtime In-Line Human Review & Rejection   | • Live Web Search  |
| I +------------------+-----------------------------------------------+   Doc Cards        |
| T | Bottom Panel                                                     +--------------------+
| Y | • xterm.js Realtime Agent Trace  • Vitest/JUnit Test Dashboard  • Problems & Lints    |
|   +---------------------------------------------------------------------------------------+
|   | Status Bar: Progress: 85% | Fidelity Score: 99.2% | Node 24 | [Submit PR] [Export ZIP] |
+---+---------------------------------------------------------------------------------------+
```

---

## ⚡ Execution Modes: Full-Auto & Human-in-the-Loop

```mermaid
stateDiagram-v2
    [*] --> Ingestion
    Ingestion: Repository Ingestion
    Ingestion --> DependencyAnalysis
    DependencyAnalysis: Dependency Analysis

    state "Execution Mode Selection" as ModeSelection
    DependencyAnalysis --> ModeSelection

    ModeSelection --> FullAuto: Default Mode
    ModeSelection --> InteractiveHITL: Ambiguity Detected

    state "Interactive Mode (grill-me)" as InteractiveHITL {
        [*] --> GrillMeInterview
        GrillMeInterview: Trigger Decision Card
        GrillMeInterview --> UserApproval
        UserApproval: Awaiting Decision Choice
        UserApproval --> TransformerStep
        TransformerStep: Decision Applied
    }

    state "Full-Auto Mode" as FullAuto {
        [*] --> BestPracticeSelection
        BestPracticeSelection: Apply Best Practice
        BestPracticeSelection --> TransformerStep
    }

    TransformerStep --> ASTValidation
    ASTValidation: Run AST & Syntax Check
    ASTValidation --> SelfReflection: Syntax Error Detected
    SelfReflection: Self-Reflection & Retry
    SelfReflection --> TransformerStep

    ASTValidation --> QATesting: Syntax OK
    QATesting: Run Regression Tests
    QATesting --> DiffReview: Test Report Ready

    state "Diff Review" as DiffReview {
        [*] --> SideBySideMonaco
        SideBySideMonaco: Side-by-Side Monaco Editor
        SideBySideMonaco --> AcceptAll: One-Click Accept
        SideBySideMonaco --> PartialReject: Inline Edit / AI Re-generate
    }

    DiffReview --> DeliverablePipeline
    DeliverablePipeline: GitHub PR / ZIP Export
    DeliverablePipeline --> [*]
```

---

## 🚀 Quick Start (Development)

### Prerequisites
- **Node.js**: `24.x LTS`
- **Package Manager**: `pnpm` (recommended) or `npm`
- **LLM API Key**: `DEEPSEEK_API_KEY` (DeepSeek-v4-pro)

### 1. Clone Repository
```bash
git clone https://github.com/your-org/legacy-code-modernizer.git
cd legacy-code-modernizer
```

### 2. Backend Setup
```bash
cd Backend
pnpm install
pnpm dev
# Server running at http://localhost:4000
```

### 3. Frontend Setup
```bash
cd ../Frontend
pnpm install
pnpm dev
# Workbench accessible at http://localhost:5173
```

---

## 📚 Technical Documentation Index

For in-depth architectural specifications and implementation protocols, please explore the `/docs` directory:

- 📐 [**System Architecture & Runtime Specification**](./docs/architecture.md) ([中文版](./docs/zh/architecture.md)): Full breakdown of Node 24 runtime, AST toolchains, and sandbox isolation.
- 🤖 [**Agent Orchestration & Protocol Specification**](./docs/agent-orchestration.md) ([中文版](./docs/zh/agent-orchestration.md)): DeepSeek-v4-pro inference matrix, ReAct loops, SSE protocols, and `grill-me` skill.
- 🗺️ [**Modernization Tracks & Migration Matrix**](./docs/migration-matrix.md) ([中文版](./docs/zh/migration-matrix.md)): In-depth conversion rules for JSP, Python, Vue/React, and Node ecosystems.
- 🧪 [**Benchmark Test Suites & Quantifiable Metrics**](./docs/benchmark-test-suites.md) ([中文版](./docs/zh/benchmark-test-suites.md)): Ground-truth test datasets, code contracts, and deterministic preservation scoring formulas.
- 🌐 [**Deployment & Tunneling Specification**](./docs/deployment-and-tunneling.md) ([中文版](./docs/zh/deployment-and-tunneling.md)): Cloudflare Tunnel, Ngrok setup, and keep-alive configuration for hackathon presentations.

---

## 📄 License

This project is licensed under the [MIT License](./LICENSE).