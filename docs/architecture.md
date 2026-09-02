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
        Xterm["xterm.js Streaming Console"]
        Flow["XYFlow Business Dependency Visualizer"]
        UI --> Panels
        Panels --> Monaco
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
            WM --> FS_Source
            WM --> FS_Target
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

        subgraph VerificationSandbox ["Execution & Test Runner Sandbox"]
            Runner["Process Execution & Test Sandbox"]
            Vitest["Vitest / Jest Runner"]
            PyTest["PyTest Python Runner"]
            JUnit["JUnit 5 Test Harness"]
            Runner --> Vitest
            Runner --> PyTest
            Runner --> JUnit
        end

        Gateway --> WM
        Gateway --> Orch
        Orch --> ASTToolchain
        Orch --> VerificationSandbox
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
    WM-->>API: Workspace Initialized { sessionId, fileTree }
    API-->>UI: 200 OK + Render Dual File Tree
```

---

## 3. AST Toolchain & Static Code Analysis

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

## 4. Frontend Workbench Component Architecture

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
        RationaleBadge["AI Modification Rationale Tooltips"]
        InlineReview["Accept / Reject Inline Actions"]
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
