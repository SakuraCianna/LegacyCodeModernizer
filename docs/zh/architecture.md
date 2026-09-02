# 📐 系统架构与运行时规范 (System Architecture & Runtime)

<p align="center">
  <a href="../architecture.md">English Version</a> | <a href="./architecture.md">简体中文版</a>
</p>

---

## 1. 系统全景与分层架构

**Legacy Code Modernizer** 是一个基于 **Node.js 24 LTS** 与 **DeepSeek-v4-pro** 的高吞吐、事件驱动全栈系统。展示层与 Agent 执行核心通过 RESTful API 与 Server-Sent Events (SSE) 保持双向解耦与实时流式互通。

```mermaid
flowchart TD
    subgraph ClientLayer ["1. 用户展示层 (VS Code Web 工作台)"]
        UI["React 19 现代化应用"]
        Monaco["Monaco Diff 双栏差异编辑器"]
        LivePreview["iframe 独立实时渲染沙箱"]
        Xterm["xterm.js 实时流式控制台"]
        Flow["XYFlow 业务依赖拓扑图"]
        UI --- Monaco
        UI --- LivePreview
        UI --- Xterm
        UI --- Flow
    end

    subgraph GatewayLayer ["2. 网关与状态层 (Node.js 24 Fastify)"]
        REST["REST API 路由网关"]
        SSE["SSE 实时事件流推送"]
        OAuth["GitHub OAuth 单点登录"]
        SQLite["嵌入式 SQLite 数据库 (WAL 模式)"]
        Workspaces["多租户目录 /workspaces/:username/:sessionId"]
        REST --- SSE
        REST --- OAuth
        REST --- SQLite
        REST --- Workspaces
    end

    subgraph AgentLayer ["3. 三 Agent 自主核心 (DeepSeek-v4-pro)"]
        Arch["🧠 架构与业务全景分析师"]
        Trans["🛠️ 现代代码重构工程师"]
        Test["🧪 业务保真与测试工程师"]
        LLM["DeepSeek-v4-pro 推理引擎 (Prompt Cache 锁定)"]
        Arch <--> LLM
        Trans <--> LLM
        Test <--> LLM
    end

    subgraph ExecutionLayer ["4. AST 智能分析与分层沙箱"]
        AST["确定性 AST 工具链 (Tree-Sitter, Babel, ts-morph)"]
        Sandbox["分层沙箱 (Vitest 线程, 2GB 熔断 Java/Py, MicroVM)"]
        AST --- Sandbox
    end

    subgraph DeliveryLayer ["5. 成果交付流水线"]
        PR["GitHub Pull Request 自动提交与 CI 检查"]
        ZIP["现代化工程源码 ZIP 打包与审计报告"]
    end

    ClientLayer <-->|RESTful 与 SSE 协议| GatewayLayer
    GatewayLayer <-->|异步事件总线| AgentLayer
    AgentLayer <-->|确定性工具调用| ExecutionLayer
    AgentLayer -->|编译打包交付| DeliveryLayer
```

---

## 2. GitHub OAuth 统一认证与 SQLite 存储架构

系统采用 **GitHub OAuth 一键单点登录 (SSO)**。用户无需手动生成或配置 GitHub Personal Access Token (PAT)，OAuth 鉴权成功后系统即可直接以用户身份安全拉取私有代码库并自动创建 PR：

```mermaid
sequenceDiagram
    autonumber
    actor Developer as "开发者"
    participant UI as "VS Code Web 工作台"
    participant API as "Fastify 认证网关"
    participant GitHub as "GitHub OAuth 授权服务器"
    participant DB as "SQLite 数据库 (local.db)"

    Developer->>UI: 点击 "Login with GitHub"
    UI->>API: GET /api/auth/github/login
    API-->>UI: 302 重定向至 GitHub 授权页
    UI->>GitHub: 用户确认授权范围 (read:user, repo)
    GitHub-->>API: 携带授权码回调 ?code=xyz
    API->>GitHub: POST /login/oauth/access_token { code, client_id, client_secret }
    GitHub-->>API: 返回 { access_token, scope }
    API->>GitHub: GET /user (获取用户信息与 GitHub ID)
    GitHub-->>API: 返回 { id, login, avatar_url, email }
    API->>DB: 写入或更新用户 Profile 与加密 Token
    API-->>UI: 写入安全 HttpOnly Session JWT + 进入工作台
```

### 2.1 嵌入式 SQLite 数据库表结构 (`local.db`)

```sql
-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    github_id INTEGER UNIQUE NOT NULL,
    username TEXT NOT NULL,
    avatar_url TEXT,
    access_token TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 工作区元数据表
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

-- 文件快照与审计日志表
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

---

## 3. 多租户物理目录隔离规范

磁盘工作区按用户登录名 (`username`) 进行物理隔离，从底层切断跨用户文件越权风险：

```text
/workspaces
  ├── /octocat/                               # GitHub 用户: octocat
  │     └── /sess_9a8b7c6d/                   # 会话 ID
  │           ├── /source/                    # 只读原工程目录
  │           ├── /target/                    # 可写现代化产物目录
  │           └── /snapshots/                 # 版本历史快照 (v1, v2...)
  │                 └── /src/App.vue/
  │                       ├── v1.snap
  │                       └── v2.snap
  └── /sakuracianna/                          # GitHub 用户: sakuracianna
        └── /sess_1e2f3a4b/
              ├── /source/
              ├── /target/
              └── /snapshots/
```

---

## 4. 代码版本快照与文件并发锁控制引擎

```mermaid
flowchart TD
    Req["Transformer Agent 发起文件修改请求"] --> AcquireLock{"竞争获取文件独占锁"}
    AcquireLock -->|锁被占用| Wait["退避等待重试 (上限3次)"]
    Wait --> AcquireLock
    AcquireLock -->|成功获取锁| ReadCurr["读取目标文件当前内容与版本号 (vN)"]
    ReadCurr --> Snapshot["自动生成快照 /snapshots/filePath/vN.snap"]
    Snapshot --> Apply["应用双轨补丁 (新建全量生成 / 局部Search-Replace)"]
    Apply --> ValidateAST{"AST 语法完整性校验"}
    ValidateAST -->|语法错误| Rollback["自动回退至快照 vN 并记录失败原因"]
    Rollback --> ReleaseLock["释放文件独占锁"]
    ValidateAST -->|语法通过| BumpVer["提交新代码并升级版本号为 vN+1"]
    BumpVer --> EmitSSE["向前端推送 file_diff_chunk (携带新版本号 vN+1)"]
    EmitSSE --> ReleaseLock
```

---

## 5. 分层执行与实时渲染沙箱架构 (借鉴 Claude Artifacts)

```mermaid
graph TD
    subgraph Tier1_Client ["第 1 层：前端 iframe 隔离渲染沙箱 (现代组件实时预览)"]
        Iframe["独立 iframe (sandbox='allow-scripts allow-forms')"]
        EsbuildWasm["esbuild-wasm / Babel 浏览器端毫秒级编译"]
        EsmCDN["ESM CDN 模块动态加载 (esm.sh / unpkg)"]
        Iframe --> EsbuildWasm
        EsbuildWasm --> EsmCDN
    end

    subgraph Tier2_Backend ["第 2 层：Node 24 原生线程与受限子进程 (测试驱动)"]
        NodeWorkers["Node.js 24 worker_threads (内存级 Vitest 驱动)"]
        GuardedProcess["受限子进程 (执行 Java / PyTest)"]
        TimeoutGuard["10秒硬超时熔断 (SIGKILL)"]
        MemoryCap["2048MB (2GB) 内存上限约束"]
        MockStubs["内存级 Mock 桩 (respx / H2 / MockMvc)"]
        GuardedProcess --> TimeoutGuard
        GuardedProcess --> MemoryCap
        GuardedProcess --> MockStubs
    end

    subgraph Tier3_Enterprise ["第 3 层：企业级云端 MicroVM 接口 (可插拔扩展)"]
        E2B_Adapter["E2B / Firecracker MicroVM 网关 (低延迟冷启动)"]
        FullCLI["完整 Linux Shell 与沙箱守护进程"]
        E2B_Adapter --> FullCLI
    end
```

---

## 6. 多语言 AST 解析与静态分析流水线

```mermaid
flowchart LR
    subgraph InputCode ["源工程文件"]
        JSP["JSP / Java 文件"]
        PY["Python 2 脚本"]
        VUE["Vue 2 / React 组件"]
        JS["Node CJS 文件"]
    end

    subgraph Parsers ["多语言 AST 解析器"]
        TS_Java["Tree-Sitter Java 语法解析器"]
        TS_Py["Tree-Sitter Python 语法解析器"]
        Babel_Vue["@babel/parser + Vue 模板编译器"]
        TS_Morph["ts-morph TypeScript 引擎"]
    end

    subgraph Analysis ["静态代码智能分析"]
        SymbolMap["全局符号引用与 Import/Export 映射表"]
        DepOrder["自底向上依赖拓扑排序器"]
        SyntaxValidator["重构后语法与类型校验器"]
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

## 7. 前端 VS Code 风格工作台组件架构

```mermaid
graph TB
    subgraph MainAppRoot ["主应用入口组件 (App.tsx)"]
        ActivityBar["Activity Bar 活动栏组件 (用户头像、工作区切换)"]
        PanelGroup["Resizable Panel Group 响应式面板群"]
        StatusBar["底部全局状态栏 (保真度评分、进度条)"]
        ActivityBar --- PanelGroup
        PanelGroup --- StatusBar
    end

    subgraph SidebarViews ["左侧侧边栏子视图"]
        FileTree["双工程对照文件树 (Legacy vs Target)"]
        BusinessMap["XYFlow 业务数据流与拓扑映射图"]
        TaskRoadmap["现代化任务拆解清单"]
    end

    subgraph EditorViews ["中央编辑区子视图"]
        MonacoDiff["Monaco 双栏并排差异对比编辑器"]
        LiveSandboxView["iframe 现代化组件实时预览沙箱"]
        VersionSelector["版本快照切换器 (v1, v2, v3 一键回退)"]
        RationaleBadge["AI 行级重构原因与改动批注"]
        MonacoDiff --- VersionSelector
    end

    subgraph AgentViews ["右侧 Agent 辅助栏子视图"]
        AgentChat["三 Agent 实时对话与思考轨迹"]
        ToolBadge["工具调用与 AST 检查实时卡片"]
        DocSnippet["官方文档联网检索摘要卡片"]
        GrillCard["grill-me 架构决策交互卡片"]
    end

    subgraph BottomViews ["底部调试面板子视图"]
        XtermTerminal["xterm.js 实时 Agent 终端日志流"]
        TestRunner["Vitest / JUnit 自动化测试执行看板"]
        LinterProblems["静态代码诊断与问题清单"]
    end

    MainAppRoot --> SidebarViews
    MainAppRoot --> EditorViews
    MainAppRoot --> AgentViews
    MainAppRoot --> BottomViews
```
