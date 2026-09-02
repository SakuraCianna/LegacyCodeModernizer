# 🚀 Legacy Code Modernizer (遗留系统智能现代化迁移与重构工作台)

<p align="center">
  <strong>自主驱动的企业级遗留系统现代化重构与智能迁移工作台</strong><br>
  <em>确保核心业务逻辑零破坏，实现跨生态全仓自动化升级</em>
</p>

<p align="center">
  <a href="./README.md"><strong>English</strong></a> | <a href="./README_zh.md"><strong>简体中文</strong></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Node.js-24%20LTS-339933?style=flat-square&logo=node.js&logoColor=white" alt="Node.js 24 LTS" />
  <img src="https://img.shields.io/badge/大模型基座-DeepSeek--v4--pro-4f46e5?style=flat-square" alt="DeepSeek-v4-pro" />
  <img src="https://img.shields.io/badge/架构-Tri--Agent%20ReAct-6366f1?style=flat-square" alt="Tri-Agent ReAct" />
  <img src="https://img.shields.io/badge/前端界面-VS%20Code%20IDE%20Style-007ACC?style=flat-square&logo=visualstudiocode&logoColor=white" alt="VS Code Web UI" />
  <img src="https://img.shields.io/badge/AST引擎-Tree--Sitter%20%7C%20Babel-f59e0b?style=flat-square" alt="AST Engine" />
  <img src="https://img.shields.io/badge/质量验证-自动化回归测试-10b981?style=flat-square" alt="Verification" />
</p>

---

## 🌟 Agent 开场白与核心定位

> **“专注企业级遗留系统智能现代化改造，深度聚焦 JSP ➔ Java/Spring Boot 生态升级、Python 生态升级、Vue/React 生态升级与 Node 生态升级四大技术赛道。基于 DeepSeek-v4-pro 大模型底座，驱动三 Agent 专家团队、AST 级精准代码转换、官方文档实时联网检索与回归测试沙箱验证，实现业务逻辑零破坏的自动化全仓重构。”**

---

## 💡 痛点与核心价值

企业老旧代码库（如 JSP 单体模板、Python 2 脚本、Vue 2 Options 组件、Node CommonJS 回调地狱）承载着核心业务，却伴随着沉重的技术债与安全隐患：
1. **人工重构成本极高**：跨版本、多文件的全仓迁移通常耗费资深工程师数周乃至数月的时间；
2. **隐式业务逻辑脆弱**：历史边缘场景（Edge Cases）缺少文档与完备测试，人工重写极易漏掉隐式依赖而引发线上事故；
3. **传统 AI 辅助的局限性**：简单的单次 Prompt 或 Copilot 补全无法理解全仓依赖拓扑，极易幻觉出已弃用的 API，且无法处理大仓跨文件上下文。

**Legacy Code Modernizer** 选用 **DeepSeek-v4-pro** 作为全栈推理底座，结合 **Prompt Caching 前缀锁定**、**确定性 AST 工具链** 与 **轻量测试验证沙箱**，提供开箱即用的专业 VS Code 风格 Web 工作台。

---

## 🎯 四大核心现代化赛道

```mermaid
mindmap
  root((Legacy Code Modernizer))
    JSP 转 Java / Spring Boot
      JSP 表单标签转现代 DTO 与 REST API
      Struts/Servlet 转 Spring Boot 3.x
      Session 状态机制转无状态 JWT / Spring Security
      Java 8 升级至 Java 21 LTS
    Python 生态升级
      Python 2.7 升级至 Python 3.12+
      老旧 Flask/Django 升级至 FastAPI/现代 Django
      类型注解 Injection 与现代 Asyncio 异步化
      Six 与 urllib2 等弃用库彻底清理
    Vue / React 生态升级
      Vue 2 Options API 迁移至 Vue 3 Script Setup & TS
      React 早期 Class 组件重构为 React 19 Hooks
      jQuery DOM 强操作转声明式响应状态
      Vuex 迁移至 Pinia / Redux 迁移至 Zustand
    Node.js 生态升级
      CommonJS require 升级至原生 ESM import
      Callback 回调地狱重构为 Async / Await
      老旧 Express 迁移至 Fastify / NestJS
      底层引擎对齐 Node.js 24 LTS
```

| 现代化赛道 | 典型源技术栈 (Legacy) | 目标现代化栈 (Target) | 核心转换能力与业务保真保障 |
| :--- | :--- | :--- | :--- |
| **JSP ➔ Java/Spring Boot** | JSP 标签、Struts、Servlet、Java 8 | Spring Boot 3.x、RESTful API、现代前端组件、Java 21 | 表单标签转现代 DTO、Session 机制转 Token 鉴权、SQL 注入与弃用 API 清理 |
| **Python 生态升级** | Python 2.7、老旧 Flask/Django、`urllib2`、`six` | Python 3.12+、FastAPI、类型注解 (`typing`)、`asyncio` | 字符串/字节流转换、弃用标准库平滑替换、异步并发重构 |
| **Vue / React 生态升级** | Vue 2 Options API、React Class 组件、jQuery | Vue 3 `<script setup>`、React 19 Hooks + TypeScript + Tailwind | 响应式语法精准迁移、生命周期钩子对齐、状态管理平滑升级 |
| **Node.js 生态升级** | CommonJS (`require`)、Callback 地狱、Express 3/4 | 原生 ESM (`import`)、Async/Await、Fastify、Node 24 LTS | 静态模块解析重构、Promise 流程编排、内存泄漏排查 |

---

## 🏛️ 系统架构

```mermaid
graph TD
    subgraph Client ["用户交互层 (VS Code 风格 Web 工作台)"]
        A1["1-Click 经典靶场 Demo"] --> UI["工作区交互面板"]
        A2["本地 ZIP 拖拽上传"] --> UI
        A3["GitHub URL + Token 链接"] --> UI
        UI --> Diff["Monaco 双栏并排 Diff 编辑器"]
        UI --> Term["xterm.js 实时 Agent 终端日志"]
        UI --> Topo["XYFlow 业务链路拓扑图"]
        UI --> QA["质量验证与测试看板"]
    end

    subgraph Communication ["双向通信与流式推送层"]
        UI <-->|RESTful API| HTTP["API 网关"]
        UI <-->|Server-Sent Events / SSE| Stream["实时事件流推送器"]
    end

    subgraph Engine ["Node.js 24 LTS 后端 Agent 运行时"]
        HTTP --> Orch["三 Agent 协同调度器"]
        Stream <--> Orch

        subgraph Agents ["自主专家 Agent 矩阵"]
            Ag1["🧠 Modernize Architect<br/>(架构与业务全景分析师)"]
            Ag2["🛠️ Code Transformer<br/>(现代代码重构工程师)"]
            Ag3["🧪 Test & Quality Verifier<br/>(业务保真与测试工程师)"]
        end

        Orch --> Ag1
        Orch --> Ag2
        Orch --> Ag3

        subgraph LLM_Layer ["LLM 推理与上下文缓存层"]
            DS["DeepSeek-v4-pro 推理引擎"]
            Cache["Prompt Caching 静态前缀锁定"]
            Cache --> DS
        end

        Ag1 <--> LLM_Layer
        Ag2 <--> LLM_Layer
        Ag3 <--> LLM_Layer

        subgraph Skills ["内置专属技能库 (Skills)"]
            Sk1["💬 grill-me 决策技能<br/>(重大架构分支追问交互)"]
            Sk2["🗺️ 业务全景建模技能<br/>(端到端业务流与依赖分析)"]
            Sk3["🔍 官方文档联网检索<br/>(在线抓取最新升级与 Breaking 指南)"]
        end

        Ag1 -.-> Sk1
        Ag1 -.-> Sk2
        Ag2 -.-> Sk3

        subgraph Toolbox ["确定性工具箱 (Toolbox)"]
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

    subgraph Deliverables ["成果交付流水线"]
        Orch --> PR["GitHub Pull Request 自动提交与变更日志"]
        Orch --> ZIP["现代化工程源码 ZIP 打包下载"]
    end
```

---

## 🤖 三 Agent 专家团队与技能协同

```mermaid
sequenceDiagram
    autonumber
    actor User as "用户 / 开发者"
    participant Arch as "🧠 架构分析师 (Architect)"
    participant Trans as "🛠️ 重构工程师 (Transformer)"
    participant Test as "🧪 测试工程师 (Verifier)"
    participant Tools as "🧰 AST 与工具沙箱"

    User->>Arch: 导入遗留代码库 (靶场/ZIP/Git)
    Arch->>Tools: 扫描全局符号，构建 AST 依赖拓扑图
    Tools-->>Arch: 返回拓扑排序队列与业务数据流

    opt 存在重大架构决策分歧
        Arch->>User: 触发 "grill-me" 交互式追问卡片
        User-->>Arch: 确认架构偏好 (如 REST API vs Thymeleaf 模板)
    end

    Arch->>Trans: 下发全仓分步现代化迁移计划
    loop 自底向上遍历每个组件
        Trans->>Tools: 读取代码切片，检查类型引用
        opt 遇到生僻或已弃用 API
            Trans->>Tools: 联网检索官方 Migration Guide
        end
        Trans->>Tools: 写入 AST 级现代重构补丁 (DeepSeek-v4-pro)
        Trans->>Tools: 调用静态编译器验证语法与类型
        alt 发现语法或类型错误
            Trans->>Trans: 触发自我反思与修正循环 (Self-Reflection)
        end
    end

    Trans->>Test: 交付重构后的现代化工程
    Test->>Tools: 提取历史逻辑分支，自动生成现代单元测试
    Test->>Tools: 在沙箱中执行回归测试套件
    Tools-->>Test: 返回测试执行结果
    Test-->>User: 实时推送业务保真度评分 (如 99.4%)
    Test->>User: 一键导出现代化 ZIP / 提交 GitHub PR
```

---

## 🖥️ VS Code 风格开发者工作台

遵循低视觉干扰、高信息密度的专业工程化设计：

```
+---+------------------+-----------------------------------------------+--------------------+
| A | 侧边栏 (Sidebar) | 主编辑区 (Editor Area)                        | 辅助栏 (Agent Hub) |
| C | • 双工程文件对比树 | • Monaco Side-by-Side Diff 差异高亮视图       | • 3 个 Agent 对话/ |
| T | • 业务链路拓扑图 | • 行级重构原因批注 (AI Rationale)             |   实时思考与决策   |
| I | • 迁移任务清单   | • 顶部多标签管理                              | • 联网搜索文档卡片 |
| V +------------------+-----------------------------------------------+   • grill-me 决策  |
| I | 底部面板 (Bottom Panel)                                          +--------------------+
| T | • Xterm.js 实时 Agent 终端日志  • 单元测试与保真度看板 (Vitest/JUnit)  • 静态诊断 (Problems) |
| Y +---------------------------------------------------------------------------------------+
|   | 底部状态栏 (Status Bar): 迁移进度 85% | 业务保真度: 99.2% | Node 24 | [一键提交 PR] [下载 ZIP] |
+---+---------------------------------------------------------------------------------------+
```

---

## ⚡ 执行模式：一键全自动 vs 人机协同介入 (HITL)

```mermaid
stateDiagram-v2
    [*] --> Ingestion
    Ingestion: 仓库摄入
    Ingestion --> DependencyAnalysis
    DependencyAnalysis: 依赖分析

    state "执行模式选择" as ModeSelection
    DependencyAnalysis --> ModeSelection

    ModeSelection --> FullAuto: 默认全自动
    ModeSelection --> InteractiveHITL: 识别到架构分歧

    state "专家交互模式 (grill-me)" as InteractiveHITL {
        [*] --> GrillMeInterview
        GrillMeInterview: 弹出决策卡片
        GrillMeInterview --> UserApproval
        UserApproval: 等待用户确认
        UserApproval --> TransformerStep
        TransformerStep: 确认执行方案
    }

    state "全自动模式" as FullAuto {
        [*] --> BestPracticeSelection
        BestPracticeSelection: 应用行业最佳实践
        BestPracticeSelection --> TransformerStep
    }

    TransformerStep --> ASTValidation
    ASTValidation: 语法与AST静态校验
    ASTValidation --> SelfReflection: 发现语法或类型错误
    SelfReflection: 自我反思与修复
    SelfReflection --> TransformerStep

    ASTValidation --> QATesting: 语法校验通过
    QATesting: 自动化回归测试
    QATesting --> DiffReview: 产出保真度评分

    state "Diff 差异审查" as DiffReview {
        [*] --> SideBySideMonaco
        SideBySideMonaco: Monaco 并排视图
        SideBySideMonaco --> AcceptAll: 一键全部确认
        SideBySideMonaco --> PartialReject: 局部行级驳回
    }

    DiffReview --> DeliverablePipeline
    DeliverablePipeline: 成果交付流水线 (PR / ZIP)
    DeliverablePipeline --> [*]
```

---

## 🚀 本地开发快速上手

### 环境要求
- **Node.js**：`24.x LTS`
- **包管理器**：`pnpm`（推荐）或 `npm`
- **大模型 API Key**：`DEEPSEEK_API_KEY` (DeepSeek-v4-pro)

### 1. 克隆代码仓库
```bash
git clone https://github.com/your-org/legacy-code-modernizer.git
cd legacy-code-modernizer
```

### 2. 后端服务启动
```bash
cd Backend
pnpm install
pnpm dev
# 后端服务运行于 http://localhost:4000
```

### 3. 前端工作台启动
```bash
cd ../Frontend
pnpm install
pnpm dev
# 浏览器访问 http://localhost:5173 即可进入工作台
```

---

## 📚 技术规范文档索引

详细的底层设计细节、通信协议与基准测试集请查阅 `/docs` 目录：

- 📐 [**系统架构与运行时规范**](./docs/zh/architecture.md) ([English](./docs/architecture.md))：Node.js 24 运行时架构、AST 解析器工具链与沙箱隔离时序。
- 🤖 [**Agent 协同机制与协议规范**](./docs/zh/agent-orchestration.md) ([English](./docs/agent-orchestration.md))：DeepSeek-v4-pro 推理矩阵、ReAct 状态机、SSE 流式事件协议与 `grill-me` 技能交互。
- 🗺️ [**现代化赛道与转换矩阵**](./docs/zh/migration-matrix.md) ([English](./docs/migration-matrix.md))：四大赛道 AST 转换规则、弃用 API 映射与业务保真守卫。
- 🧪 [**基准测试集与量化指标规范**](./docs/zh/benchmark-test-suites.md) ([English](./docs/benchmark-test-suites.md))：四大赛道端到端基准源码、测试断言与数学保真度评分计算公式。

---

## 📄 开源许可证

本项目基于 [MIT License](./LICENSE) 开源协议。
