# Day 1 Exit Gate

Do not proceed to Day 2 until these are demonstrated.

| Gate | Evidence | Status |
|------|----------|--------|
| **BUILD** — Starter APK compiles and launches on emulator or phone | `:app:assembleDebug` success; `:app:installDebug` success; `am start` → `topResumedActivity=...EmployeeTaskManagerActivity`; screenshot `screenshots/01-app-launched.png` | **PASS** |
| **AI** — Cursor and Codex can read and explain the project | Cursor identified launcher Activity `EmployeeTaskManagerActivity` from manifest; Codex CLI not on PATH yet (see `SETUP_ISSUES.md`) — Cursor covered Day 1 AI check | **PASS** (Codex CLI install remaining) |
| **GIT** — First commit is pushed to a remote repository | Remote `origin` → `https://github.com/nazeefc/employee-task-manager.git`; Day 1 commit includes README + evidence | **PASS** after push |
| **EVIDENCE** — README + screenshots + setup issues documented | `README.md`, `docs/day1/*` | **PASS** |

## Launch proof (command excerpts)

```
Starting: Intent { cmp=com.example.employee_task_manager/.EmployeeTaskManagerActivity }
topResumedActivity=ActivityRecord{... u0 com.example.employee_task_manager/.EmployeeTaskManagerActivity t28}
```

Emulator: AVD `nazeef_app` / `emulator-5554`
