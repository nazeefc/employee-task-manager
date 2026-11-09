# AI Handoff & Context Document: Employee Task Manager (`employeetaskmanager`)

## 1. Project Identity and Purpose
- **Project Name:** Employee Task Manager (`employeetaskmanager`)
- **Purpose:** An Android application developed as part of the Android Developer Bootcamp. The app's intended functionality enables employees to view tasks, add tasks, mark tasks complete, and see their status.

## 2. Technology and Package Details
- **Package Name:** `com.example.employee_task_manager`
- **Core Source Directory:** `app/src/main/java/com/example/employee_task_manager/`
- **Target SDK / Min SDK:** Android SDK Platform 35 (min SDK: Android 7.0 / API 24)
- **Language / Toolchain:** Kotlin, Java 11 compatibility, Android Gradle Plugin, Gradle Wrapper (`gradlew`).

## 3. Important Source-Code and Documentation Paths
- **Activities & UI:**
  - `app/src/main/java/com/example/employee_task_manager/EmployeeTaskManagerActivity.kt`
  - `app/src/main/java/com/example/employee_task_manager/ui/TaskAdapter.kt`
- **Data & Layouts:**
  - `app/src/main/java/com/example/employee_task_manager/data/Task.kt`
  - `app/src/main/res/layout/employeetaskmanager.xml`
  - `app/src/main/res/layout/item_task.xml`
- **Documentation:**
  - `AGENTS.md`
  - `README.md`
  - `docs/CURRENT_STATUS.md`
  - `docs/ARCHITECTURE.md`
  - `docs/DEVELOPMENT_PLAN.md`
  - `docs/DECISIONS.md`
  - `docs/DEBUGGING_LOG.md`

## 4. What is Currently Implemented
- RecyclerView task dashboard hosting the `Task` data model and `TaskAdapter`.
- Four static sample tasks populated in `EmployeeTaskManagerActivity`.
- Refresh Tasks button with functional click listener (reloads the same four static sample tasks; does not fetch data from a database or API).
- Corrected Completed/Pending status mapping and color rendering in `TaskAdapter`.
- Cross-device edge-to-edge window insets handling (`ViewCompat.setOnApplyWindowInsetsListener`) preventing top bar overlap on API 35.

## 5. What is Not Implemented Yet
- Add task feature / input controls.
- `TaskViewModel` (lifecycle/StateFlow management).
- Local persistence (Room Database is outlined in the development plan but is not implemented yet).

## 6. Current Git Branch and Pending Changes
- **Current Git Branch:** `training/agent-context` (required branch successfully created).
- **Pending Changes:** `docs/CURRENT_STATUS.md` is currently modified and uncommitted.
- **File Status:** `AI_HANDOFF.md` has been created.
- **Commit Status:** No commit or push has been performed.

## 7. Gemini Agent and Codex Responsibilities
- **Gemini Agent:** Project exploration, approved implementation, and technical explanations.
- **Codex:** Independent code review and identification of potential issues.

## 8. Agent Permissions and Approval Restrictions
- **Plan Before Coding:** Always follow the planning workflow and obtain trainer/user approval before modifying any application code.
- **Allowed:** Read/inspect code, create/update documentation files (`docs/`, `AGENTS.md`, etc.), create architecture diagrams.
- **Not Allowed:** Modify Kotlin/Java source code, modify XML layouts, modify Gradle files, implement application features, delete/rename existing files, or install dependencies without approval.
- **Diff Review:** Require agents to present diffs for review and obtain user approval before final acceptance of changes.

## 9. Official Week 2 Trainer Schedule
- **Day 1:** Configure AI agents, save context, test handoff.
- **Day 2:** Plan MVVM + API, diagrams, Codex review.
- **Day 3:** Integrate REST API, UI states, verify responses.
- **Day 4–5:** Debug 3 defects, add tests and review, PR + APK.
- **Day 6:** Revision, explanation, demonstration.

## 10. Current Day 1 Completion and Remaining Day 1 Tasks
- **Completed:**
  - Gemini Agent configuration and AGENTS.md setup.
  - Project context documentation review.
  - Required Git branch `training/agent-context` successfully created.
  - `docs/CURRENT_STATUS.md` status update.
  - `AI_HANDOFF.md` created.
- **Remaining Day 1 Work:**
  - Codex review of two concrete code risks (read-only).
  - Fresh Gemini session handoff verification using `AI_HANDOFF.md`.
  - Capture required Day 1 evidence screenshots.
  - Final diff review and commit only after explicit user approval.

## 11. Explicit Instruction Not to Start Day 2 Work
- **CRITICAL:** Keep Day 2 activities separate and not started. Do not begin MVVM + API planning, architecture diagrams, or REST API integration.
