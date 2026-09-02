# 📐 System Architecture & Runtime Specification

<p align="center">
  <a href="./architecture.md">English Version</a> | <a href="./zh/architecture.md">简体中文版</a>
</p>

---

## 1. System Overview & Layered Architecture

**Legacy Code Modernizer** is built on a high-throughput, event-driven fullstack architecture centered around **Node.js 24 LTS** and powered by **DeepSeek-v4-pro**. The presentation layer is decoupled from the agentic execution core via bidirectional RESTful APIs and Server-Sent Events (SSE).

```mermaid
graph TD
    subgraph ClientLayer ["Presentation Layer (VS Code Web Workbench)"]
        UI["React 19 + Vite Application"]
        Panels["react-resizable-panels Layout Engine"]
        Monaco["Monaco Diff & Code Editor Engine"]
        LivePreview["Isolated Iframe Live Rendering Sandbox"]
        Xterm["xterm.js Streaming Console"]
        Flow["XYFlow Business Dependency Visualizer"]
        UI --> Panels
        Panels --> Monaco
        Panels --> LivePreview
        Panels --> Xterm
        Panels --> Flow
    end

    subgraph NetworkLayer ["Transport & Streaming Layer"]
        REST["RESTful API: Repo Ingestion, Config, PR Trigger"]
        SSE["Server-Sent Events: Realtime Thought & Diff Streams"]
        UI <-->|JSON / Multipart| REST
        UI <--|text/event-stream| SSE
    end

    subgraph BackendRuntime ["Node.js 24 LTS Core Runtime Engine"]
        Gateway["Fastify HTTP & SSE Gateway"]
        REST --> Gateway
        Gateway --> SSE

        subgraph WorkspaceManager ["Workspace & Storage Isolation"]
            WM["Session-Isolated Workspace Controller"]
            FS_Source["/workspaces/:id/source (Readonly)"]
            FS_Target["/workspaces/:id/target (Mutable)"]
            Snapshots["/workspaces/:id/snapshots (Versioned History)"]
            LockMgr["File Lock & Concurrency Controller"]
            WM --> FS_Source
            WM --> FS_Target
            WM --> Snapshots
            WM --> LockMgr
        end

        subgraph AgentCore ["Autonomous Agent Orchestrator"]
            Orch["ReAct Multi-Agent Dispatcher"]
            Arch["🧠 Modernize Architect Agent"]
            Trans["🛠️ Code Transformer Agent"]
            Test["🧪 Test & Quality Verifier Agent"]
            Orch --> Arch
            Orch --> Trans
            Orch --> Test
        end

        subgraph LLM_Layer ["LLM Inference & Cache Layer"]
            DS["DeepSeek-v4-pro Engine"]
            Cache["Prompt Caching (Prefix Lock)"]
            Cache --> DS
        end

        Arch <--> LLM_Layer
        Trans <--> LLM_Layer
        Test <--> LLM_Layer

        subgraph ASTToolchain ["Deterministic AST & Static Analysis"]
            TS["Tree-Sitter Multi-Language Engine"]
            Babel["@babel/parser & @babel/traverse"]
            Morph["ts-morph TypeScript Compiler Engine"]
            VueCompiler["vue-template-compiler & @vue/compiler-sfc"]
        end

        subgraph TieredSandbox ["Tiered Verification Sandbox"]
            WorkerPool["Node 24 Worker Threads (In-Process Vitest)"]
            SubprocessRunner["Guarded Subprocess (Java/PyTest with 10s Timeout)"]
            MicroVMAdapter["Pluggable MicroVM Adapter (E2B / Firecracker)"]
            TieredSandbox --> WorkerPool
            TieredSandbox --> SubprocessRunner
            TieredSandbox --> MicroVMAdapter
        end

        Gateway --> WM
        Gateway --> Orch
        Orch --> ASTToolchain
        Orch --> TieredSandbox
    end

    subgraph CloudVCS ["External Ecosystems & VCS"]
        GitHub["GitHub REST / GraphQL API - PR Pipeline"]
        WebDoc["Web Search Engine / Official Documentation CDNs"]
        Gateway <--> GitHub
        Orch <--> WebDoc
    end
```

---

## 2. Ingestion & Workspace Isolation Pipeline

To guarantee data safety and performance during code analysis, every repository ingestion generates an ephemeral, isolated workspace session:

```mermaid
sequenceDiagram
    autonumber
    actor Developer as "Developer"
    participant UI as "VS Code Web Workbench"
    participant API as "Fastify Gateway"
    participant WM as "Workspace Manager"
    participant FS as "Ephemeral File System"

    alt 1-Click Preset Demo
        Developer->>UI: Select Preset (e.g. JSP Blog / Vue 2 Cart)
        UI->>API: POST /api/workspace/preset { presetId }
        API->>WM: Instantiate Preset Template
    else ZIP Upload
        Developer->>UI: Drag & Drop legacy-project.zip
        UI->>API: POST /api/workspace/upload (Multipart Stream)
        API->>WM: Decompress & Filter Blacklisted Dirs (.git, node_modules, target)
    else GitHub Ingestion
        Developer->>UI: Submit Repo URL + PAT Token
        UI->>API: POST /api/workspace/clone { repoUrl, token }
        API->>WM: Execute Shallow Clone (git clone --depth 1)
    end

    WM->>FS: Allocate /workspaces/{sessionId}/source/ (Read-Only)
    WM->>FS: Allocate /workspaces/{sessionId}/target/ (Modernized Output)
    WM->>FS: Allocate /workspaces/{sessionId}/snapshots/ (Version Control)
    WM-->>API: Workspace Initialized { sessionId, fileTree }
    API-->>UI: 200 OK + Render Dual File Tree
```

---

## 3. Code Snapshot Versioning & Optimistic Lock Engine

To support one-click rollback in the Monaco Diff editor and prevent race conditions when multiple agent steps touch the same file, the backend maintains a **File-Level Snapshot & Lock Control Protocol**:

```mermaid
flowchart TD
    subgraph FileMutationFlow ["File Mutation & Snapshot Workflow"]
        Req["Transformer Agent Mutation Request"] --> AcquireLock{"Acquire File Lock"}
        AcquireLock -->|Lock Busy| Wait["Backoff & Retry (Max 3)"]
        Wait --> AcquireLock
        AcquireLock -->|Lock Acquired| ReadCurr["Read Current File & Current Version (vN)"]
        ReadCurr --> Snapshot["Save Snapshot to /snapshots/filePath/vN.snap"]
        Snapshot --> Apply["Apply Dual-Track Patch (Whole-File or Search/Replace)"]
        Apply --> ValidateAST{"AST Syntax Check"}
        ValidateAST -->|Syntax Error| Rollback["Auto Rollback to vN & Increment Retry"]
        Rollback --> ReleaseLock["Release File Lock"]
        ValidateAST -->|Syntax OK| BumpVer["Commit Target File as vN+1"]
        BumpVer --> EmitSSE["Emit file_diff_chunk with version=vN+1"]
        EmitSSE --> ReleaseLock
    end
```

---

## 4. Tiered Sandbox & Live Rendering Architecture (Claude-Inspired)

Inspired by Anthropic Claude's Artifacts and Cloud Sandbox architectures, execution is tiered into browser-side zero-latency isolation and backend resource-guarded runners:

```mermaid
graph TD
    subgraph Tier1_Client ["Tier 1: Client-Side Iframe Sandbox (Live UI Preview)"]
        Iframe["Isolated iframe (sandbox='allow-scripts')"]
        EsbuildWasm["esbuild-wasm / Babel In-Browser Bundler"]
        EsmCDN["ESM CDN Imports (esm.sh / unpkg)"]
        Iframe --> EsbuildWasm
        EsbuildWasm --> EsmCDN
    end

    subgraph Tier2_Backend ["Tier 2: Node 24 In-Process & Guarded Runner (Testing)"]
        NodeWorkers["Node.js 24 worker_threads (In-Process Vitest)"]
        GuardedProcess["Subprocess Runner with Strict Resource Caps"]
        TimeoutGuard["10-Second Hard Timeout (SIGKILL)"]
        MemoryCap["2048MB (2GB) RAM Limit"]
        MockStubs["In-Memory Mock Fixtures (respx / H2 / MockMvc)"]
        GuardedProcess --> TimeoutGuard
        GuardedProcess --> MemoryCap
        GuardedProcess --> MockStubs
    end

    subgraph Tier3_Enterprise ["Tier 3: Cloud MicroVM Interface (Pluggable)"]
        E2B_Adapter["E2B / Firecracker MicroVM Gateway (5ms Cold Boot)"]
        FullCLI["Full Linux Shell & Sandbox Daemon Execution"]
        E2B_Adapter --> FullCLI
    end
```

### 4.1 Client-Side Live Component Preview
- Modernized Vue 3 and React 19 components render inside a strictly isolated `<iframe sandbox="allow-scripts allow-forms">` without `allow-same-origin`.
- Dependencies (Tailwind, Lucide icons, Vue, React) are dynamically imported via `https://esm.sh`, providing instant visual verification of zero UI disruption.

### 4.2 Backend Test Execution Guardrails
- **Vitest In-Process Runner**: JS/TS tests run within Node.js 24 `worker_threads` with zero process-spawn overhead.
- **Java / Python Subprocess Guard**:
  - **10s Hard Timeout**: Kills runaway loops instantly.
  - **Memory Limits**: Caps heap allocations at 2048MB (2GB).
  - **Mock Fixtures**: Injects mock network and in-memory database adapters to run tests without external database dependencies.

---

## 5. AST Toolchain & Static Code Analysis

The backend integrates specialized Abstract Syntax Tree (AST) parsers to parse, validate, and manipulate code without losing formatting or introducing syntax hallucinations:

```mermaid
flowchart LR
    subgraph InputCode ["Source Files"]
        JSP["JSP / Java Files"]
        PY["Python 2 Files"]
        VUE["Vue 2 / React Files"]
        JS["Node CJS Files"]
    end

    subgraph Parsers ["Multi-Language AST Parsers"]
        TS_Java["Tree-Sitter Java Parser"]
        TS_Py["Tree-Sitter Python Parser"]
        Babel_Vue["@babel/parser + Vue SFC Compiler"]
        TS_Morph["ts-morph TypeScript Engine"]
    end

    subgraph Analysis ["Static Code Intelligence"]
        SymbolMap["Symbol Reference & Export/Import Graph"]
        DepOrder["Topological Dependency Sorter"]
        SyntaxValidator["Post-Transformation Syntax Verifier"]
    end

    JSP --> TS_Java
    PY --> TS_Py
    VUE --> Babel_Vue
    JS --> TS_Morph

    TS_Java --> SymbolMap
    TS_Py --> SymbolMap
    Babel_Vue --> SymbolMap
    TS_Morph --> SymbolMap

    SymbolMap --> DepOrder
    DepOrder --> SyntaxValidator
```

---

## 6. Frontend Workbench Component Architecture

The frontend is modeled after the VS Code workbench to maximize information density and developer familiarity:

```mermaid
graph TB
    subgraph App ["Main Application Root (App.tsx)"]
        ActivityBar["Activity Bar Component"]
        PanelGroup["Resizable Panel Group"]
        StatusBar["Global Status Bar"]

        ActivityBar --> PanelGroup
        PanelGroup --> LeftSidebar["Primary Sidebar Component"]
        PanelGroup --> CenterEditor["Editor Area Component"]
        PanelGroup --> RightSidebar["Secondary Agent Hub"]
        PanelGroup --> BottomPanel["Bottom Panel Component"]
    end

    subgraph LeftSidebarComponents ["Primary Sidebar Views"]
        FileTree["Dual File Tree: Source vs Target"]
        BusinessMap["XYFlow Business Dependency Topology"]
        TaskRoadmap["Modernization Task Checklist"]
    end

    subgraph CenterEditorComponents ["Editor Views"]
        MonacoDiff["Monaco Side-by-Side Diff Editor"]
        LiveSandboxView["Iframe Live UI Preview Sandbox"]
        VersionSelector["File Snapshot Version Switcher (v1, v2...)"]
        RationaleBadge["AI Modification Rationale Tooltips"]
        InlineReview["Accept / Reject Inline Actions"]
        MonacoDiff --> VersionSelector
    end

    subgraph RightSidebarComponents ["Agent Hub Views"]
        AgentChat["Tri-Agent Live Dialogue & Thought Trace"]
        ToolBadge["Tool Call & AST Inspection Cards"]
        DocSnippet["Live Web Search Snippet Cards"]
        GrillCard["grill-me Decision Cards"]
    end

    subgraph BottomPanelComponents ["Bottom Panel Views"]
        XtermTerminal["xterm.js Live Streaming Terminal"]
        TestRunner["Vitest / JUnit Test Results Grid"]
        LinterProblems["Problems & Diagnostics Tab"]
    end

    LeftSidebar --> LeftSidebarComponents
    CenterEditor --> CenterEditorComponents
    RightSidebar --> RightSidebarComponents
    BottomPanel --> BottomPanelComponents
```
