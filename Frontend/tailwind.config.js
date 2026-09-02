/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        vscode: {
          bg: "#1e1e1e",
          sidebar: "#252526",
          activityBar: "#333333",
          editorBg: "#1e1e1e",
          border: "#3c3c3c",
          statusBar: "#007acc",
          accent: "#0e639c"
        }
      }
    }
  },
  plugins: []
};
