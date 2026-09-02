import React from "react";
import { Terminal, Code2, GitBranch, Cpu, Sparkles } from "lucide-react";

/**
 * Legacy Code Modernizer - Workbench Root Shell Skeleton
 * Pure layout skeleton placeholder before implementing full agent business orchestration.
 */
export const App: React.FC = () => {
  return (
    <div className="flex h-screen w-screen flex-col bg-[#1e1e1e] text-[#cccccc] select-none font-mono">
      {/* Top Header / Menubar */}
      <header className="flex h-9 items-center justify-between border-b border-[#3c3c3c] bg-[#333333] px-3 text-xs text-[#cccccc]">
        <div className="flex items-center gap-2 font-semibold">
          <Sparkles className="h-4 w-4 text-[#007acc]" />
          <span>Legacy Code Modernizer</span>
          <span className="rounded bg-[#252526] px-1.5 py-0.5 text-[10px] text-[#858585]">
            v0.1.0-alpha
          </span>
        </div>
        <div className="flex items-center gap-4 text-[#858585]">
          <span>Node.js 24 LTS</span>
          <span>DeepSeek-v4-pro</span>
        </div>
      </header>

      {/* Main Workbench Body Shell */}
      <main className="flex flex-1 overflow-hidden">
        {/* Activity Bar */}
        <nav className="flex w-12 flex-col items-center border-r border-[#3c3c3c] bg-[#333333] py-2">
          <button
            title="Explorer"
            className="flex h-10 w-10 items-center justify-center text-[#ffffff] hover:bg-[#2a2d2e]"
          >
            <Code2 className="h-5 w-5" />
          </button>
          <button
            title="Source Control"
            className="flex h-10 w-10 items-center justify-center text-[#858585] hover:bg-[#2a2d2e]"
          >
            <GitBranch className="h-5 w-5" />
          </button>
          <button
            title="Agent Matrix"
            className="flex h-10 w-10 items-center justify-center text-[#858585] hover:bg-[#2a2d2e]"
          >
            <Cpu className="h-5 w-5" />
          </button>
        </nav>

        {/* Primary Sidebar Placeholder */}
        <aside className="w-64 border-r border-[#3c3c3c] bg-[#252526] p-3 text-xs">
          <div className="text-[11px] font-bold tracking-wider text-[#858585] uppercase">
            Workspace Explorer
          </div>
          <div className="mt-3 text-[#858585]">
            No legacy repository loaded. Select a 1-Click Demo or import a repository.
          </div>
        </aside>

        {/* Central Editor Area Placeholder */}
        <section className="flex flex-1 flex-col bg-[#1e1e1e]">
          <div className="flex h-8 items-center border-b border-[#3c3c3c] bg-[#252526] px-3 text-xs text-[#858585]">
            <span>Welcome.tsx</span>
          </div>
          <div className="flex flex-1 items-center justify-center text-sm text-[#858585]">
            <div className="text-center">
              <Sparkles className="mx-auto mb-2 h-8 w-8 text-[#007acc]" />
              <p className="font-semibold text-[#cccccc]">Legacy Code Modernizer Workbench</p>
              <p className="mt-1 text-xs">Autonomous Tri-Agent Refactoring Engine</p>
            </div>
          </div>
        </section>

        {/* Secondary Agent Hub Placeholder */}
        <aside className="w-80 border-l border-[#3c3c3c] bg-[#252526] p-3 text-xs">
          <div className="text-[11px] font-bold tracking-wider text-[#858585] uppercase">
            Tri-Agent Hub
          </div>
          <div className="mt-3 text-[#858585]">
            Architect, Transformer & Verifier agents standing by.
          </div>
        </aside>
      </main>

      {/* Global Status Bar */}
      <footer className="flex h-6 items-center justify-between bg-[#007acc] px-3 text-xs text-white">
        <div className="flex items-center gap-3">
          <span className="flex items-center gap-1">
            <Terminal className="h-3.5 w-3.5" />
            <span>Ready</span>
          </span>
          <span>Preservation Fidelity: --</span>
        </div>
        <div>
          <span>UTF-8 | LF | TypeScript</span>
        </div>
      </footer>
    </div>
  );
};

export default App;
