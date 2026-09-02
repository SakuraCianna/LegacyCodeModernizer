# 📐 系统架构与运行时规范 (System Architecture & Runtime)

<p align="center">
  <a href="../architecture.md">English Version</a> | <a href="./architecture.md">简体中文版</a>
</p>

---

## 1. 系统全景与分层架构

**Legacy Code Modernizer** 是一个基于 **Node.js 24 LTS** 与 **DeepSeek-v4-pro** 的高吞吐、事件驱动全栈系统。展示层与 Agent 执行核心通过 RESTful API 与 Server-Sent Events (SSE) 保持双向解耦与实时流式互通。

```mermaid
graph TD
    subgraph ClientLayer ["用户展示层 (VS Code Web 工作台)"]
        UI["React 19 + Vite 现代化应用"]
        Panels["react-resizable-panels 布局引擎"]
        Monaco["Monaco Diff 双栏差异编辑器"]
        LivePreview["iframe 独立实时渲染沙箱"]
        Xterm["xterm.js 实时流式控制台"]
        Flow["XYFlow 业务依赖拓扑图"]
        UI --> Panels
        Panels --> Monaco
        Panels --> LivePreview
        Panels --> Xterm
        Panels --> Flow
    end

    subgraph NetworkLayer ["传输与实时推送层"]
        REST["RESTful API: 代码库导入、配置、PR 触发"]
        SSE["Server-Sent Events: 实时推送 Agent 思考与 Diff 流"]
        UI <-->|JSON / Multipart| REST
        UI <--|text/event-stream| SSE
    end

    subgraph BackendRuntime ["Node.js 24 LTS 后端核心运行时"]
        Gateway["Fastify 高性能 HTTP & SSE 网关"]
        REST --> Gateway
        Gateway --> SSE

        subgraph WorkspaceManager ["工作区与数据安全隔离"]
            WM["会话级临时工作区控制器"]
            FS_Source["/workspaces/:id/source (只读原工程)"]
            FS_Target["/workspaces/:id/target (现代化产物)"]
            Snapshots["/workspaces/:id/snapshots (历史版本快照)"]
            LockMgr["文件并发锁与版本控制器"]
            WM --> FS_Source
            WM --> FS_Target
            WM --> Snapshots
            WM --> LockMgr
        end

        subgraph AgentCore ["自主 Agent 协同调度引擎"]
            Orch["ReAct 多 Agent 分发调度器"]
            Arch["🧠 架构与业务全景分析师"]
            Trans["🛠️ 现代代码重构工程师"]
            Test["🧪 业务保真与测试工程师"]
            Orch --> Arch
            Orch --> Trans
            Orch --> Test
        end

        subgraph LLM_Layer ["LLM 推理与上下文缓存层"]
            DS["DeepSeek-v4-pro 推理引擎"]
            Cache["Prompt Caching 静态前缀锁定"]
            Cache --> DS
        end

        Arch <--> LLM_Layer
        Trans <--> LLM_Layer
        Test <--> LLM_Layer

        subgraph ASTToolchain ["确定性 AST 与静态分析工具链"]
            TS["Tree-Sitter 多语言统一解析引擎"]
            Babel["@babel/parser 与 @babel/traverse"]
            Morph["ts-morph TypeScript 编译器"]
            VueCompiler["vue-template-compiler 与 @vue/compiler-sfc"]
        end

        subgraph TieredSandbox ["分层测试与验证沙箱"]
            WorkerPool["Node 24 Worker Threads (进程内 Vitest 驱动)"]
            SubprocessRunner["受限子进程 (Java/PyTest 10秒超时熔断)"]
            MicroVMAdapter["可插拔云端 MicroVM 接口 (E2B / Firecracker)"]
            TieredSandbox --> WorkerPool
            TieredSandbox --> SubprocessRunner
            TieredSandbox --> MicroVMAdapter
        end

        Gateway --> WM
        Gateway --> Orch
        Orch --> ASTToolchain
        Orch --> TieredSandbox
    end

    subgraph CloudVCS ["外部生态与代码托管平台"]
        GitHub["GitHub REST / GraphQL API - PR 提交"]
        WebDoc["官方文档检索 CDN / 搜索引擎 API"]
        Gateway <--> GitHub
        Orch <--> WebDoc
    end
```

---

## 2. 仓库导入与工作区会话隔离时序

为确保代码绝对安全与重构前后的对照完整性，每次导入均生成独立的会话工作区目录：

```mermaid
sequenceDiagram
    autonumber
    actor Developer as "开发者"
    participant UI as "VS Code Web 工作台"
    participant API as "Fastify 网关"
    participant WM as "工作区管理器"
    participant FS as "临时文件系统"

    alt 1-Click 经典靶场 Demo
        Developer->>UI: 选择内置靶场 (如 JSP 博客 / Vue 2 购物车)
        UI->>API: POST /api/workspace/preset { presetId }
        API->>WM: 实例化靶场模板工程
    else 本地 ZIP 上传
        Developer->>UI: 拖拽上传 legacy-project.zip
        UI->>API: POST /api/workspace/upload (Multipart 流)
        API->>WM: 解压并过滤黑名单目录 (.git, node_modules, target 等)
    else GitHub 链接导入
        Developer->>UI: 提交仓库 URL 与 PAT Token
        UI->>API: POST /api/workspace/clone { repoUrl, token }
        API->>WM: 执行浅克隆 (git clone --depth 1)
    end

    WM->>FS: 初始化 /workspaces/{sessionId}/source/ (原工程只读目录)
    WM->>FS: 初始化 /workspaces/{sessionId}/target/ (重构产物可写目录)
    WM->>FS: 初始化 /workspaces/{sessionId}/snapshots/ (历史版本快照目录)
    WM-->>API: 返回工作区初始化状态 { sessionId, fileTree }
    API-->>UI: 200 OK + 渲染双工程对照文件树
```

---

## 3. 代码版本快照与文件并发锁控制引擎

为支持在前端 Monaco Diff 编辑器中进行**任意历史版本一键回退**，并彻底杜绝多个 Agent 或重试循环对同一文件的并发修改冲突，后端内置了**文件级版本快照与并发锁控制协议（OCC）**：

```mermaid
flowchart TD
    subgraph FileMutationFlow ["文件修改与版本快照流水线"]
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
    end
```

---

## 4. 分层执行与实时渲染沙箱架构 (借鉴 Claude Artifacts)

借鉴 Anthropic Claude Artifacts 与 MicroVM 沙箱设计，系统将代码执行划分为浏览器端零延迟渲染与服务端资源受限测试双层沙箱：

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
        MemoryCap["512MB 内存上限约束"]
        MockStubs["内存级 Mock 桩 (respx / H2 / MockMvc)"]
        GuardedProcess --> TimeoutGuard
        GuardedProcess --> MemoryCap
        GuardedProcess --> MockStubs
    end

    subgraph Tier3_Enterprise ["第 3 层：企业级云端 MicroVM 接口 (可插拔扩展)"]
        E2B_Adapter["E2B / Firecracker MicroVM 网关 (5ms 极速冷启动)"]
        FullCLI["完整 Linux Shell 与沙箱守护进程"]
        E2B_Adapter --> FullCLI
    end
```

### 4.1 前端 UI 实时渲染沙箱
- 重构后的 Vue 3 与 React 19 组件直接在 `<iframe sandbox="allow-scripts allow-forms">`（不开放 `allow-same-origin`）中安全运行；
- 依赖项（Tailwind、Lucide 图标、Vue 3、React）通过 `esm.sh` CDN 动态按需加载，无需本地安装即可直观验证“UI 视觉与交互零破坏”。

### 4.2 后端测试执行与熔断保护
- **Vitest 进程内执行器**：JS/TS 测试用例直接在 Node.js 24 `worker_threads` 内存线程中秒级运行，零进程开销；
- **Java / Python 子进程守卫**：
  - **10 秒硬超时熔断**：防止死循环阻塞测试流水线；
  - **512MB 内存限制**：防止堆内存溢出；
  - **Mock 隔离桩**：全量注入网络与内存数据库桩，无需外部复杂数据库即可独立完成测试回归。

---

## 5. 多语言 AST 解析与静态分析流水线

后端结合底层 Tree-Sitter 与专用编译器，实现高保真语法树解析与符号映射：

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

## 6. 前端 VS Code 风格工作台组件架构

```mermaid
graph TB
    subgraph App ["主应用入口 (App.tsx)"]
        ActivityBar["Activity Bar 活动栏组件"]
        PanelGroup["Resizable Panel Group 响应式面板群"]
        StatusBar["底部全局状态栏"]

        ActivityBar --> PanelGroup
        PanelGroup --> LeftSidebar["左侧侧边栏 (Primary Sidebar)"]
        PanelGroup --> CenterEditor["中央主编辑区 (Editor Area)"]
        PanelGroup --> RightSidebar["右侧 Agent 辅助栏 (Agent Hub)"]
        PanelGroup --> BottomPanel["底部调试面板 (Bottom Panel)"]
    end

    subgraph LeftSidebarComponents ["左侧侧边栏子视图"]
        FileTree["双工程对照文件树 (Legacy vs Target)"]
        BusinessMap["XYFlow 业务数据流与拓扑映射图"]
        TaskRoadmap["现代化任务拆解清单"]
    end

    subgraph CenterEditorComponents ["中央编辑区子视图"]
        MonacoDiff["Monaco 双栏并排差异对比编辑器"]
        LiveSandboxView["iframe 现代化组件实时预览沙箱"]
        VersionSelector["版本快照切换器 (v1, v2, v3 一键回退)"]
        RationaleBadge["AI 行级重构原因与改动批注"]
        InlineReview["单行/单函数采纳与驳回控件"]
        MonacoDiff --> VersionSelector
    end

    subgraph RightSidebarComponents ["右侧辅助栏子视图"]
        AgentChat["三 Agent 实时对话与思考轨迹"]
        ToolBadge["工具调用与 AST 检查实时卡片"]
        DocSnippet["官方文档联网检索摘要卡片"]
        GrillCard["grill-me 架构决策交互卡片"]
    end

    subgraph BottomPanelComponents ["底部调试面板子视图"]
        XtermTerminal["xterm.js 实时 Agent 终端日志流"]
        TestRunner["Vitest / JUnit 自动化测试执行看板"]
        LinterProblems["静态代码诊断与问题清单"]
    end

    LeftSidebar --> LeftSidebarComponents
    CenterEditor --> CenterEditorComponents
    RightSidebar --> RightSidebarComponents
    BottomPanel --> BottomPanelComponents
```
