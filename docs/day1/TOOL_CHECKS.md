# Day 1 — Tool verification

Commands from the bootcamp Day 1 slide, run on Windows PowerShell (Nov 3, 2026).

## Results

| Command | Result |
|---------|--------|
| `java -version` | **FAIL** — `java` not recognized on PATH (Studio/Gradle JDK still builds the app) |
| `git --version` | `git version 2.55.0.windows.5` |
| `node -v` | `v22.23.2` |
| `npm -v` | `10.9.8` |
| `adb version` | Android Debug Bridge 1.0.41 (37.0.1-15733141) |
| `codex --version` | **FAIL** — Codex CLI not installed / not on PATH |

## Build / launch

- Gradle: `:app:assembleDebug` — **SUCCESS**
- Emulator: AVD `nazeef_app` → `emulator-5554`
- Package: `com.example.employee_task_manager`
- Launcher Activity: `.EmployeeTaskManagerActivity`

## AI check (Cursor)

Cursor identified the main Activity as `EmployeeTaskManagerActivity` from `AndroidManifest.xml` (`MAIN` / `LAUNCHER`) and summarized the starter project structure.

## Screenshots

Place or keep device/emulator captures in `docs/day1/screenshots/`:

- `01-app-launched.png` — starter app on emulator
- Optional: terminal tool-check outputs
