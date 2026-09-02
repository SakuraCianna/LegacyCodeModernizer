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
        Xterm["xterm.js 实时流式控制台"]
        Flow["XYFlow 业务依赖拓扑图"]
        UI --> Panels
        Panels --> Monaco
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
            WM --> FS_Source
            WM --> FS_Target
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

        subgraph VerificationSandbox ["轻量测试与验证沙箱"]
            Runner["进程沙箱与测试执行器"]
            Vitest["Vitest / Jest 运行器"]
            PyTest["PyTest Python 运行器"]
            JUnit["JUnit 5 测试套件驱动"]
            Runner --> Vitest
            Runner --> PyTest
            Runner --> JUnit
        end

        Gateway --> WM
        Gateway --> Orch
        Orch --> ASTToolchain
        Orch --> VerificationSandbox
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
    WM-->>API: 返回工作区初始化状态 { sessionId, fileTree }
    API-->>UI: 200 OK + 渲染双工程对照文件树
```

---

## 3. 多语言 AST 解析与静态分析流水线

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

## 4. 前端 VS Code 风格工作台组件架构

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
        RationaleBadge["AI 行级重构原因与改动批注"]
        InlineReview["单行/单函数采纳与驳回控件"]
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
