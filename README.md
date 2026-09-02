# 🚀 Legacy Code Modernizer (遗留系统智能现代化迁移与重构工作台)

> **Agent 开场白 / 核心定位：**  
> 专注企业级遗留系统智能现代化改造，深度聚焦 **JSP ➔ Java/Spring Boot 生态升级**、**Python 生态升级**、**Vue/React 生态升级**与 **Node 生态升级** 四大技术赛道，通过自主 Agent 团队与业务保真测试，实现业务逻辑零破坏的自动化全仓重构。

---

## 📖 1. 项目背景与解决痛点

在企业数字化演进中，大量核心业务被困在老旧技术栈中（如 JSP 模板、Python 2 脚本、Vue 2 Options API、老旧 CommonJS/回调地狱）。人工迁移面临三大核心痛点：
1. **耗时耗力与极高的人力成本**：大仓跨版本重构往往需要数周至数月；
2. **极易破坏隐式业务逻辑**：历史边缘场景（Edge Cases）缺乏文档与完备测试，重构即翻车；
3. **传统 AI 辅助的局限性**：简单的单次 Prompt / 插件翻译无法理解全仓依赖，存在严重的 API 幻觉与上下文丢失。

**Legacy Code Modernizer** 借鉴 `Claude Code` 与 `grok-build` 的自驱动编程 Agent 架构，将大模型置于具备全套 AST 分析、代码检索、联网搜索官方文档与自动化测试能力的 Agent 运行时中，提供全流程闭环的现代化升级工作台。

---

## 🎯 2. 四大核心迁移赛道 (Supported Ecosystems)

| 迁移赛道 | 典型源技术栈 (Legacy) | 目标现代化栈 (Target) | 核心转换能力与业务保真保障 |
| :--- | :--- | :--- | :--- |
| **JSP ➔ Java/Spring Boot** | JSP 标签、Struts/Servlet、Java 8 | Spring Boot 3.x、RESTful API、Thymeleaf/现代前端、Java 21 | 表单标签转现代 DTO、Session 机制转 JWT/Token、SQL 注入与弃用 API 清理 |
| **Python 生态升级** | Python 2.7、旧版 Flask/Django、`urllib2` | Python 3.12+、FastAPI/现代 Django、类型注解 (Typing)、`asyncio` | 字符串/字节流转换、弃用内置库替换、依赖项自动锁定与安全升级 |
| **Vue/React 生态升级** | Vue 2 Options API / React 早期 Class 组件、jQuery | Vue 3 `<script setup>` / React 19 Hooks + TS + Tailwind | 响应式语法平滑迁移、状态管理转 Pinia/Zustand、生命周期精准映射 |
| **Node 生态升级** | CommonJS (`require`)、Callback 地狱、旧版 Express | Native ESM (`import`)、Async/Await、Fastify/NestJS、Node 24 LTS | 模块解析重构、Promise 流程编排、类型完备性注解与内存泄漏排查 |

---

## 🏗️ 3. 系统核心架构与 Agent 矩阵

系统基于 **Node.js 24 LTS** 全栈构建，采用自研的 **三 Agent 协同调度引擎（Tri-Agent Orchestration Loop）**：

```
                              +-------------------------------------------+
                              |         用户交互层 (VS Code Web IDE)       |
                              |  - 1-Click 靶场 Demo / ZIP / GitHub Token  |
                              |  - Monaco Side-by-Side Diff 对比          |
                              |  - 实时 Agent 思考终端 & 业务拓扑流看板     |
                              +---------------------+---------------------+
                                                    | (SSE / REST)
                                                    v
+---------------------------------------------------------------------------------------------------------+
|                                    Node.js 24 后端 Agent 运行时                                          |
|                                                                                                         |
|   +--------------------------+  +-------------------------------+  +--------------------------------+   |
|   | 🧠 Modernize Architect   |  | 🛠️ Code Transformer           |  | 🧪 Test & Quality Verifier     |   |
|   | (架构与业务全景分析师)   |  | (现代代码重构工程师)          |  | (业务保真与测试工程师)         |   |
|   | - 依赖拓扑排序与解耦     |  | - 语法树级别代码转换          |  | - 自动化生成现代测试用例       |   |
|   | - 提取端到端业务流链路   |  | - 知识库与迁移规则注入        |  | - 执行逻辑回归比对             |   |
|   | - 输出《全仓迁移计划》   |  | - 自我修正循环 (Self-Fix)     |  | - 输出《业务保真度评分报告》   |   |
|   +------------+-------------+  +---------------+---------------+  +---------------+----------------+   |
|                |                                |                                  |                    |
|                +--------------------------------+----------------------------------+                    |
|                                                 | (调度工具集 Tool Calls)                                |
|                                                 v                                                       |
|   +-------------------------------------------------------------------------------------------------+   |
|   |                                       Agent 工具箱 (Toolbox)                                     |   |
|   |  • search_symbols_and_deps: AST 符号与跨文件依赖分析                                            |   |
|   |  • read_source_file: 代码切片按需读取与上下文精准装配                                           |   |
|   |  • search_official_docs: 联网检索官方最新版本升级指南与 Breaking Changes                        |   |
|   |  • apply_code_patch: 目标工程文件精准 Patch / 重写                                              |   |
|   |  • verify_syntax: Babel / TS / Java / Python AST 静态语法检验                                   |   |
|   |  • run_regression_tests: 容器/沙箱化测试执行器 (Vitest / Pytest / JUnit)                        |   |
|   +-------------------------------------------------------------------------------------------------+   |
+---------------------------------------------------------------------------------------------------------+
```

---

## 🖥️ 4. 前端工作台设计 (VS Code 极简工程美学)

界面遵循 **“工具属性第一、低视觉干扰、高信息密度”** 的专业工程设计原则（拒绝花里胡哨与 AI 浮夸特效）：

```
+---+------------------+-----------------------------------------------+--------------------+
| A | 侧边栏 (Sidebar) | 主编辑区 (Editor Area)                        | 辅助栏 (Agent Hub) |
| C | • 双工程文件对比树 | • Monaco Side-by-Side Diff 差异高亮视图       | • 3 个 Agent 对话/ |
| T | • 业务链路拓扑图 | • 行级重构原因批注 (AI Rationale)             |   实时思考与决策   |
| I | • 迁移任务清单   | • 顶部多标签管理                              | • 联网搜索文档卡片 |
| V +------------------+-----------------------------------------------+--------------------+
| I | 底部面板 (Bottom Panel)                                                               |
| T | • Xterm.js 实时 Agent 终端日志  • 单元测试与保真度看板 (Vitest/JUnit)  • 静态诊断 (Problems) |
| Y +---------------------------------------------------------------------------------------+
|   | 底部状态栏 (Status Bar): 迁移进度 85% | 业务保真度: 99.2% | Node 24 | [一键提交 PR] [下载 ZIP] |
+---+---------------------------------------------------------------------------------------+
```

### 核心交互特色
1. **三模输入矩阵**：
   - **1-Click 靶场 Demo**：内置 4 大生态经典老旧工程，评委零门槛一键体验；
   - **本地 ZIP 上传**：专为企业私有/内网代码设计，前端拖拽解压即用；
   - **GitHub Repo + Token**：一键浅克隆，重构完成直接反向开分支提交 Pull Request。
2. **Monaco Side-by-Side Diff**：老旧代码（红）与现代代码（绿）双栏并排，支持行级审查与人工微调。
3. **业务链路拓扑图（Domain & Business Flow Map）**：可视化展现老系统与新系统的接口与数据流迁移映射。
4. **实时终端与思考流**：通过 SSE 驱动 `xterm.js`，实时展示 Agent 在沙箱中的每一步决策、AST 检查与测试执行。

---

## 🛡️ 5. 业务逻辑零破坏与防幻觉保障体系

1. **自底向上拓扑重构**：
   - 优先重构底层数据类型与基础工具库，再重构业务服务层，最后重构入口视图层，彻底解决跨文件上下文依赖丢失问题。
2. **AST 语法约束与自省闭环（Self-Reflection Loop）**：
   - 重构代码输出后，立即通过本地 AST 编译器验证语法有效性与关键方法完整性，若发现遗漏自动触发反思重试。
3. **独立测试 Agent 与逻辑一致性验证**：
   - 提取旧系统逻辑分支，自动生成现代化单测用例（JUnit 5 / PyTest / Vitest），以测试通过率量化“业务保真度评分”。

---

## 🛠️ 6. 技术栈清单

- **后端运行时**：Node.js 24 LTS
- **后端框架**：Fastify / Hono (Native ESM, TypeScript)
- **Agent 引擎**：自研 ReAct Agent 调度器 + Tool Calling + SSE 事件流推送
- **AST / 代码分析**：`@babel/parser`, `@babel/traverse`, `ts-morph`, `tree-sitter`
- **前端框架**：React 19 + Vite + TypeScript
- **布局与样式**：Tailwind CSS + `react-resizable-panels` + `lucide-react`
- **编辑器与终端**：`@monaco-editor/react`, `@xterm/xterm`, `@xyflow/react`

---

## 📅 7. 黑客松交付路线图 (Roadmap)

- [x] **Phase 1: 概念与架构设计**（已完成：四赛道定位、三 Agent 架构、VS Code 工作台与工具链设计）
- [ ] **Phase 2: 靶场 Demo 数据集准备**（JSP 博客、Python 2 脚本、Vue 2 购物车、Node CJS 模块）
- [ ] **Phase 3: Node 24 后端 Agent 运行时与 AST 工具集搭建**
- [ ] **Phase 4: 前端 VS Code 风格工作台与 Monaco Diff 联调**
- [ ] **Phase 5: 评委路演演示脚本与一键 PR 闭环打磨**