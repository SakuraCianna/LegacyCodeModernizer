# 🤖 Agent Orchestration & Protocol Specification

<p align="center">
  <a href="./agent-orchestration.md">English Version</a> | <a href="./zh/agent-orchestration.md">简体中文版</a>
</p>

---

## 1. Tri-Agent Team & Division of Responsibilities

Inspired by modern autonomous coding agents (`Claude Code`, `grok-build`), the modernization engine coordinates three specialized agents operating in an asynchronous ReAct (Reasoning + Acting) loop powered by **DeepSeek-v4-pro**.

```mermaid
flowchart TD
    User(["Developer / User"])

    subgraph BusLayer ["Async Event Bus (Node.js 24)"]
        EventBus["Workspace Async Event Streamer"]
    end

    subgraph ArchAgent ["🧠 Modernize Architect Agent"]
        A_Prompt["Domain Modeling & Dependency Topology"]
        A_Skill["Skill: grill-me Decision Interview"]
        A_Prompt --- A_Skill
    end

    subgraph TransAgent ["🛠️ Code Transformer Agent"]
        T_Prompt["AST Dual-Track Patching & Refactoring"]
        T_Skill["Skill: Live Web Search Official Docs"]
        T_Prompt --- T_Skill
    end

    subgraph TestAgent ["🧪 Test & Quality Verifier Agent"]
        Q_Prompt["Test Synthesis & Assertion Extraction"]
        Q_Harness["Pre-Flight CI Dry-Run & Preservation Scorer"]
        Q_Prompt --- Q_Harness
    end

    subgraph LLM_Engine ["DeepSeek-v4-pro Inference Layer"]
        Cache["Prompt Caching (Prefix Lock)"]
        Gateway["OpenAI-Compatible Gateway"]
        Cache --- Gateway
    end

    User <-->|WebSocket / SSE & REST| EventBus
    EventBus <--> ArchAgent
    EventBus <--> TransAgent
    EventBus <--> TestAgent

    ArchAgent -->|1. Migration Plan & Task Queue| TransAgent
    TransAgent -->|2. Modernized Code Slices with Version Tags| TestAgent
    TestAgent -->|3. Verification & Fidelity Report| EventBus

    ArchAgent <--> LLM_Engine
    TransAgent <--> LLM_Engine
    TestAgent <--> LLM_Engine
```

---

## 2. ReAct Agent Lifecycle & State Machine

```mermaid
stateDiagram-v2
    [*] --> IDLE

    IDLE --> SCANNING: Ingestion Triggered
    state "Dependency Scanning" as SCANNING {
        [*] --> ParseASTSymbols
        ParseASTSymbols: Parse Global AST Symbols
        ParseASTSymbols --> ConstructDepGraph
        ConstructDepGraph: Construct Dependency Graph
        ConstructDepGraph --> DetectBranches
        DetectBranches: Detect Architectural Branches
    }

    SCANNING --> GRILL_ME_INTERVIEW: Major Decision Ambiguity Detected
    state "Decision Interview (grill-me)" as GRILL_ME_INTERVIEW {
        [*] --> EmitCard
        EmitCard: Emit Structured Decision Card
        EmitCard --> AwaitChoice
        AwaitChoice: Await User Selection
        AwaitChoice --> FreezePlan
        FreezePlan: Freeze Modernization Roadmap
    }

    SCANNING --> DISPATCH_PLAN: Clean Dependency Graph Ready
    GRILL_ME_INTERVIEW --> DISPATCH_PLAN

    state "Plan Dispatch" as DISPATCH_PLAN {
        [*] --> TopoSort
        TopoSort: Bottom-Up Topological Sort
        TopoSort --> QueueTasks
        QueueTasks: Queue Migration Tasks
    }

    DISPATCH_PLAN --> TRANSFORMING

    state "Code Transformation" as TRANSFORMING {
        [*] --> ReadSlice
        ReadSlice: Read Source Code Slices
        ReadSlice --> FetchDoc
        FetchDoc: Live Web Search Official Docs
        FetchDoc --> AcquireLock
        AcquireLock: Acquire File Lock & Record vN Snapshot
        AcquireLock --> GenPatch
        GenPatch: Generate Dual-Track Patch via DeepSeek-v4-pro
        GenPatch --> ApplyPatch
        ApplyPatch: Apply AST Patch (Whole-File or Search/Replace)
        ApplyPatch --> VerifySyntax
        VerifySyntax: Verify Syntax & Type Integrity
        VerifySyntax --> FixSyntax: Error Encountered
        FixSyntax: Auto Rollback to vN & Re-attempt
        FixSyntax --> ApplyPatch
        VerifySyntax --> FinalizeFile: Syntax Passed
        FinalizeFile: Commit vN+1 Snapshot & Release Lock
    }

    TRANSFORMING --> VERIFYING: All Files Modernized

    state "Testing & Verification" as VERIFYING {
        [*] --> GenTests
        GenTests: Synthesize Regression Tests
        GenTests --> RunSandbox
        RunSandbox: Execute Sandbox Tests
        RunSandbox --> CalcScore
        CalcScore: Compute Fidelity Score
    }

    VERIFYING --> CI_DRY_RUN: Fidelity Threshold Met

    state "Pre-Flight CI Dry-Run" as CI_DRY_RUN {
        [*] --> CheckPathCasing
        CheckPathCasing: Linux Case-Sensitivity Audit
        CheckPathCasing --> RunTypecheck
        RunTypecheck: Strict Type Check (tsc / mypy)
        RunTypecheck --> RunLinter
        RunLinter: Linting & Lockfile Audit
        RunLinter --> FixCIError: CI Linter/Type Failure
        FixCIError: Trigger Transformer Self-Healing
        FixCIError --> RunTypecheck
        RunLinter --> CIPassed: All Checks Green
        CIPassed: CI Dry-Run Certified
    }

    CI_DRY_RUN --> READY_FOR_REVIEW

    state "Diff Review" as READY_FOR_REVIEW {
        [*] --> StreamDiff
        StreamDiff: Stream Versioned Diff to Monaco Editor
        StreamDiff --> HumanReview
        HumanReview: Line-by-Line Review & Snapshot Stepping
        HumanReview --> GeneratePR: Accept All
        HumanReview --> RollbackStep: Rollback to vN
        RollbackStep --> RePromptTransformer
        RePromptTransformer: Re-Prompt Transformer Agent
    }

    RePromptTransformer --> TRANSFORMING
    GeneratePR --> [*]
```

---

## 3. Pre-Flight CI Dry-Run & Eliminating AI CI Failures

A frequent failure mode in autonomous coding agents is that generated code runs locally but fails when pushed to GitHub Actions CI/CD. The **Verifier Agent** eliminates this via a deterministic **Pre-Flight CI Dry-Run**:

### 3.1 Four Root Causes of AI CI Failures & Solutions

| Root Cause of CI Failure | Real-World Manifestation | Modernizer Deterministic Defense |
| :--- | :--- | :--- |
| **1. File Path Case-Sensitivity** | Windows is case-insensitive (`import './user'` matches `User.ts`), but Linux CI (`ubuntu-latest`) crashes with `Module not found`. | **AST Path Normalizer**: Audits all relative import paths against exact disk casing before submission. |
| **2. Unpinned Dependencies** | CI `npm install` picks up fresh breaking minor versions not present during generation. | **Deterministic Lockfile Generator**: Generates pinned `package-lock.json` / `pnpm-lock.yaml` with frozen versions. |
| **3. Strict Linter / Typecheck Errors** | CI enforces `tsc --noEmit` and `eslint --max-warnings=0`; raw LLMs often emit implicit `any` or unused imports. | **Local Typecheck Dry-Run**: Runs `tsc --noEmit` in sandbox; automatically re-prompts Transformer Agent on errors. |
| **4. Flaky Async Test Timers** | Tests relying on arbitrary `sleep(500)` fail on constrained CI CPU cores. | **Deterministic Test Harness**: Uses Vitest `vi.waitFor` and MockMvc asynchronous latch barriers. |

---

## 4. Full-Asset Deliverable Pipeline

When the user accepts modernizations, the system compiles a complete **Production-Ready Deliverable Package**:

```mermaid
graph LR
    subgraph Package ["Final Deliverables (.zip / GitHub PR)"]
        Src["1. Modernized Source Code (/target)"]
        Tests["2. Synthesized Test Suites (JUnit / Vitest / PyTest)"]
        Report["3. MODERNIZATION_REPORT.md (Changelog & Audit Log)"]
        CI["4. .github/workflows/ci.yml (Pre-Verified CI Pipeline)"]
    end
```

---

## 5. Dual-Track Code Patching & Version Snapshot Protocol

To eliminate off-by-one line number hallucinations and support instant rollback, the **Code Transformer Agent** uses a **Dual-Track Code Patching Strategy**:

### 5.1 Dual-Track Patch Specification
1. **Track A: Whole-File Generation (For Extracted / New Components)**: Outputs full file content directly into `/target` workspace.
2. **Track B: Structured Search & Replace Blocks (For Existing File Refactoring)**:
   ```text
   <<<<<<< SEARCH
   String action = request.getParameter("action");
   =======
   String action = request.getAction();
   >>>>>>> REPLACE
   ```
   Processed by a backend Fuzzy Block Matcher in Node 24, avoiding fragile line number indexing.

---

## 6. DeepSeek-v4-pro Inference Strategy & Optimization

### 6.1 Tri-Agent Inference Parameter Matrix

| Agent Role | Primary Objective | Temperature | Top_P | Max Tokens | Reasoning Mode | Rationale |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| 🧠 **Architect Agent** | Global dependency planning, domain modeling, `grill-me` | `0.2` | `0.95` | `8,192` | Enabled (Thinking Mode) | High reasoning depth, strictly ordered task queues |
| 🛠️ **Transformer Agent** | AST code rewrite, surgical patch creation, self-healing | `0.0` | `1.0` | `16,384` | Precision Coding Mode | **Zero randomness**, eliminates deprecated API hallucinations |
| 🧪 **Verifier Agent** | Test case synthesis, CI dry-run, assertion checking | `0.1` | `0.9` | `8,192` | Strict Validation Mode | Comprehensive assertion coverage and logic preservation |

---

## 7. SSE (Server-Sent Events) Stream Protocol

### Event Payload Schema

```typescript
export type ModernizerSSEEvent =
  | {
      type: "agent_thought";
      agent: "architect" | "transformer" | "verifier";
      step: number;
      thought: string;
      timestamp: number;
    }
  | {
      type: "tool_call";
      agent: "architect" | "transformer" | "verifier";
      toolName: string;
      parameters: Record<string, unknown>;
      timestamp: number;
    }
  | {
      type: "tool_result";
      toolName: string;
      output: string;
      status: "success" | "error";
      timestamp: number;
    }
  | {
      type: "file_diff_chunk";
      filePath: string;
      status: "in_progress" | "completed";
      patchType: "whole_file" | "search_replace";
      fileVersion: number;
      previousVersion: number;
      rollbackSupported: boolean;
      originalContent: string;
      modifiedContent: string;
      rationale: string;
      timestamp: number;
    }
  | {
      type: "file_lock_status";
      filePath: string;
      state: "acquired" | "waiting" | "released";
      heldByAgent: "transformer" | "verifier";
      timestamp: number;
    }
  | {
      type: "ci_dry_run_progress";
      stepName: "case_sensitivity" | "typecheck" | "lint" | "test";
      status: "running" | "passed" | "failed";
      errorLogs?: string;
      timestamp: number;
    }
  | {
      type: "test_suite_result";
      totalTests: number;
      passed: number;
      failed: number;
      preservationScore: number;
      testCases: Array<{
        name: string;
        status: "pass" | "fail";
        durationMs: number;
        assertion: string;
      }>;
    };
```
