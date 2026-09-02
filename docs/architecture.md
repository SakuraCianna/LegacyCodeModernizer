# 📐 System Architecture & Runtime Specification

<p align="center">
  <a href="./architecture.md">English Version</a> | <a href="./zh/architecture.md">简体中文版</a>
</p>

---

## 1. System Overview & Layered Architecture

**Legacy Code Modernizer** is built on a high-throughput, event-driven fullstack architecture centered around **Node.js 24 LTS** and powered by **DeepSeek-v4-pro**. The presentation layer is decoupled from the agentic execution core via bidirectional RESTful APIs and Server-Sent Events (SSE).

```mermaid
flowchart TD
    subgraph ClientLayer ["1. Presentation Layer (VS Code Web Workbench)"]
        UI["React 19 Web App"]
        Monaco["Monaco Diff Editor"]
        LivePreview["Iframe Live UI Sandbox"]
        Xterm["xterm.js Terminal Stream"]
        Flow["XYFlow Business Graph"]
        UI --- Monaco
        UI --- LivePreview
        UI --- Xterm
        UI --- Flow
    end

    subgraph GatewayLayer ["2. Gateway & State Layer (Node.js 24 Fastify)"]
        REST["REST API Gateway"]
        SSE["SSE Event Streamer"]
        OAuth["GitHub OAuth SSO"]
        SQLite["SQLite Embedded DB (WAL Mode)"]
        Workspaces["Multi-Tenant /workspaces/:username/:sessionId"]
        REST --- SSE
        REST --- OAuth
        REST --- SQLite
        REST --- Workspaces
    end

    subgraph AgentLayer ["3. Tri-Agent Autonomous Core (DeepSeek-v4-pro)"]
        Arch["🧠 Modernize Architect (Domain & Dep Analysis)"]
        Trans["🛠️ Code Transformer (Dual-Track Patching)"]
        Test["🧪 Test & Quality Verifier (CI Dry-Run & Scoring)"]
        LLM["DeepSeek-v4-pro Engine (Prompt Cache Prefix Lock)"]
        Arch <--> LLM
        Trans <--> LLM
        Test <--> LLM
    end

    subgraph ExecutionLayer ["4. AST Intelligence & Tiered Sandboxes"]
        AST["AST Toolchain (Tree-Sitter, Babel, ts-morph)"]
        Sandbox["Tiered Sandboxes (Vitest Workers, Guarded Java/Py 2GB, MicroVM)"]
        AST --- Sandbox
    end

    subgraph DeliveryLayer ["5. Output Deliverables Pipeline"]
        PR["GitHub Pull Request + CI Action"]
        ZIP["Modernized Source ZIP + Report"]
    end

    ClientLayer <-->|RESTful & SSE| GatewayLayer
    GatewayLayer <-->|Async Event Bus| AgentLayer
    AgentLayer <-->|Deterministic Tool Calls| ExecutionLayer
    AgentLayer -->|Compile & Export| DeliveryLayer
```

---

## 2. GitHub OAuth Authentication & SQLite Metadata Architecture

The system eliminates conventional username/password registration in favor of **1-Click GitHub OAuth SSO**, which securely provides user identity and grants automated repository/PR access without requiring manual Personal Access Tokens (PAT).

```mermaid
sequenceDiagram
    autonumber
    actor Developer as "Developer"
    participant UI as "VS Code Web Workbench"
    participant API as "Fastify Auth Gateway"
    participant GitHub as "GitHub OAuth Server"
    participant DB as "SQLite Database (local.db)"

    Developer->>UI: Click "Login with GitHub"
    UI->>API: GET /api/auth/github/login
    API-->>UI: 302 Redirect to GitHub OAuth Consent Screen
    UI->>GitHub: User Authorizes Scopes (read:user, repo)
    GitHub-->>API: Redirect Callback with ?code=xyz
    API->>GitHub: POST /login/oauth/access_token { code, client_id, client_secret }
    GitHub-->>API: Return { access_token, scope }
    API->>GitHub: GET /user (Fetch Profile & ID)
    GitHub-->>API: Return { id, login, avatar_url, email }
    API->>DB: Upsert User Profile & Encrypted Access Token
    API-->>UI: Set Secure HttpOnly Session JWT + Redirect to Workbench
```

### 2.1 Embedded SQLite Database Schema (`local.db`)

```sql
-- Users & Preferences Table
CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    github_id INTEGER UNIQUE NOT NULL,
    username TEXT NOT NULL,
    avatar_url TEXT,
    access_token TEXT NOT NULL,
    deepseek_api_key TEXT, -- User-provided DeepSeek API key (encrypted at rest)
    deepseek_base_url TEXT DEFAULT 'https://api.deepseek.com/v1',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Workspaces Metadata Table
CREATE TABLE IF NOT EXISTS workspaces (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    name TEXT NOT NULL,
    track TEXT NOT NULL, -- 'jsp_spring', 'python', 'vue_react', 'node'
    source_type TEXT NOT NULL, -- 'preset_demo', 'zip_upload', 'github_repo'
    repo_url TEXT,
    status TEXT DEFAULT 'initialized', -- 'scanning', 'transforming', 'verifying', 'completed'
    fidelity_score REAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- File Mutation Snapshots & Audit Trail
CREATE TABLE IF NOT EXISTS file_snapshots (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    workspace_id TEXT NOT NULL,
    file_path TEXT NOT NULL,
    version INTEGER NOT NULL,
    patch_type TEXT NOT NULL, -- 'whole_file', 'search_replace'
    snapshot_path TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(workspace_id) REFERENCES workspaces(id) ON DELETE CASCADE
);
```

### 2.2 Bring Your Own Key (BYOK) & Secure Multi-Tenant Persistence

To prevent context loss on page refresh or cross-device login while supporting long-running background tasks, the workbench implements a **Dual-Tier Persistence Model**:
- **Client-Side Settings**: Developers configure their `DEEPSEEK_API_KEY` (and optional custom Base URL) in the Workbench settings modal, submitted via `POST /api/user/settings`.
- **Encrypted SQLite Persistence**: The key is stored in the user's record in `users.deepseek_api_key` (encrypted at rest using `JWT_SECRET`). Even if the developer refreshes the browser, switches machines, or temporarily disconnects, their authenticated session retains the key, ensuring background ReAct tasks proceed uninterrupted.
- **Per-User Client Isolation**: Backend workers dynamically retrieve the authenticated user's key to instantiate isolated `OpenAI` client instances per session, preventing billing or quota interference.
- **Global Fallback**: The server `.env` `DEEPSEEK_API_KEY` serves solely as an optional fallback for 1-Click demo evaluation.

---

## 3. Ingestion & User-Isolated Workspace Storage

Every modernization session is strictly isolated by user handle (`username`) to eliminate data contamination between concurrent users:

```text
/workspaces
  ├── /octocat/                               # User: octocat
  │     └── /sess_9a8b7c6d/                   # Session ID
  │           ├── /source/                    # Read-only original legacy source
  │           ├── /target/                    # Mutable modernized target source
  │           └── /snapshots/                 # Versioned file history (v1, v2...)
  │                 └── /src/App.vue/
  │                       ├── v1.snap
  │                       └── v2.snap
  └── /sakuracianna/                          # User: sakuracianna
        └── /sess_1e2f3a4b/
              ├── /source/
              ├── /target/
              └── /snapshots/
```

---

## 4. Code Snapshot Versioning & Optimistic Lock Engine

To support one-click rollback in the Monaco Diff editor and prevent race conditions when multiple agent steps touch the same file, the backend maintains a **File-Level Snapshot & Lock Control Protocol**:

```mermaid
flowchart TD
    Req["Transformer Agent Mutation Request"] --> AcquireLock{"Acquire File Lock"}
    AcquireLock -->|Lock Busy| Wait["Backoff & Retry (Max 3)"]
    Wait --> AcquireLock
    AcquireLock -->|Lock Acquired| ReadCurr["Read Current File & Version (vN)"]
    ReadCurr --> Snapshot["Save Snapshot to /snapshots/filePath/vN.snap"]
    Snapshot --> Apply["Apply Dual-Track Patch (Whole-File or Search/Replace)"]
    Apply --> ValidateAST{"AST Syntax Check"}
    ValidateAST -->|Syntax Error| Rollback["Auto Rollback to vN & Increment Retry"]
    Rollback --> ReleaseLock["Release File Lock"]
    ValidateAST -->|Syntax OK| BumpVer["Commit Target File as vN+1"]
    BumpVer --> EmitSSE["Emit file_diff_chunk with version=vN+1"]
    EmitSSE --> ReleaseLock
```

---

## 5. Tiered Sandbox & Live Rendering Architecture (Claude-Inspired)

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

---

## 6. AST Toolchain & Static Code Analysis

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

## 7. Frontend Workbench Component Architecture

```mermaid
graph TB
    subgraph MainAppRoot ["Main Application Root (App.tsx)"]
        ActivityBar["Activity Bar Component (User Avatar, Workspaces)"]
        PanelGroup["Resizable Panel Group Layout"]
        StatusBar["Global Status Bar (Fidelity Score, Progress)"]
        ActivityBar --- PanelGroup
        PanelGroup --- StatusBar
    end

    subgraph SidebarViews ["Primary Sidebar Views"]
        FileTree["Dual File Tree: Source vs Target"]
        BusinessMap["XYFlow Business Dependency Topology"]
        TaskRoadmap["Modernization Task Checklist"]
    end

    subgraph EditorViews ["Center Editor Views"]
        MonacoDiff["Monaco Side-by-Side Diff Editor"]
        LiveSandboxView["Iframe Live UI Preview Sandbox"]
        VersionSelector["File Snapshot Version Switcher (v1, v2...)"]
        RationaleBadge["AI Modification Rationale Tooltips"]
        MonacoDiff --- VersionSelector
    end

    subgraph AgentViews ["Right Secondary Agent Hub Views"]
        AgentChat["Tri-Agent Live Dialogue & Thought Trace"]
        ToolBadge["Tool Call & AST Inspection Cards"]
        DocSnippet["Live Web Search Snippet Cards"]
        GrillCard["grill-me Decision Cards"]
    end

    subgraph BottomViews ["Bottom Panel Views"]
        XtermTerminal["xterm.js Live Streaming Terminal"]
        TestRunner["Vitest / JUnit Test Results Grid"]
        LinterProblems["Problems & Diagnostics Tab"]
    end

    MainAppRoot --> SidebarViews
    MainAppRoot --> EditorViews
    MainAppRoot --> AgentViews
    MainAppRoot --> BottomViews
```
