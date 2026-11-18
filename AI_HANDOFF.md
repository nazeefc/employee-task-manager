# AI Handoff & Context Document: Employee Task Manager (`employeetaskmanager`)

## 1. Project Identity and Purpose
- **Project Name:** Employee Task Manager (`employeetaskmanager`)
- **Purpose:** An Android application developed as part of the Android Developer Bootcamp. Incorporates Week 2 REST API Integration, Week 3 Day 10 Room offline storage, Day 11 WorkManager background synchronization, and Day 12 Coroutines & UI State Safety.

## 2. Technology and Package Details
- **Package Name:** `com.example.employee_task_manager`
- **Core Source Directory:** `app/src/main/java/com/example/employee_task_manager/`
- **Target SDK / Min SDK:** Android SDK Platform 35 (min SDK: Android 7.0 / API 24)
- **Language / Toolchain:** Kotlin, Java 11 compatibility, Android Gradle Plugin, Gradle Wrapper (`gradlew`).

## 3. Important Source-Code Paths
- **Activities & UI:**
  - `app/src/main/java/com/example/employee_task_manager/EmployeeTaskManagerActivity.kt`
  - `app/src/main/java/com/example/employee_task_manager/ui/DebugApiTestActivity.kt`
  - `app/src/main/java/com/example/employee_task_manager/ui/TaskAdapter.kt`
- **Data & Local Storage:**
  - `app/src/main/java/com/example/employee_task_manager/data/local/TaskEntity.kt`
  - `app/src/main/java/com/example/employee_task_manager/data/local/TaskDao.kt`
  - `app/src/main/java/com/example/employee_task_manager/data/local/AppDatabase.kt`
- **Network & Synchronization:**
  - `app/src/main/java/com/example/employee_task_manager/data/remote/TodoApiService.kt`
  - `app/src/main/java/com/example/employee_task_manager/data/remote/TaskSyncApiService.kt`
  - `app/src/main/java/com/example/employee_task_manager/data/repository/RoomTaskRepository.kt`
  - `app/src/main/java/com/example/employee_task_manager/data/worker/TaskSyncWorker.kt`
  - `app/src/main/java/com/example/employee_task_manager/ui/RoomTaskViewModel.kt`

## 4. What is Currently Implemented (Days 1–12 Complete)
- **My Tasks (Room Database v2):** Single source of truth for locally created tasks supporting offline viewing, task creation with title/description/priority, status toggling, force-stop persistence, and migration test coverage (`AppDatabaseMigrationTest`).
- **API Tasks (Week 3 Day 12 Experiment & Option B Navigation):** Integrates existing JSONPlaceholder `GET /todos` API fetching 200 remote tasks with an intentional 7-second asynchronous coroutine delay (`DelayedTodoApiService`), accessible via in-app navigation (`DebugApiTestActivity`) with a single launcher activity (`EmployeeTaskManagerActivity`).
- **Background Synchronization (Week 3 Day 11):** WorkManager `TaskSyncWorker` executing under `NetworkType.CONNECTED` constraints with exponential backoff and stable `clientId` (UUID v4) idempotency.
- **Coroutines & UI State Safety (Week 3 Day 12):**
  - Rapid completion tap protection preventing duplicate in-flight DB updates.
  - Two deterministic concurrency unit tests (`Test A` in `RoomTaskViewModelTest.kt` and `Test B` in `TaskViewModelTest.kt` verifying slow API delay, superseding requests, and stale response protection).
- **Verified Test Execution Results:**
  - Fresh Gradle execution of `.\gradlew.bat :app:testDebugUnitTest`: **27 passed, 0 failed, 0 errors, 0 skipped** (`BUILD SUCCESSFUL`).
- **Verified Build Results:**
  - Command: `.\gradlew.bat :app:assembleDebug` → **BUILD SUCCESSFUL**.
  - Debug APK Path: `app/build/outputs/apk/debug/app-debug.apk`.

## 5. Current Git Branch and Status
- **Current Git Branch:** `feature/offline-room`
- **Commit Status:** All Day 10, Day 11, and Day 12 features, concurrency tests, and documentation updates are completed locally. Independent Codex documentation re-review is **APPROVED** (4/4 findings resolved; verdict APPROVE). Git commit and push are PENDING locally on branch `feature/offline-room`.

## 6. Remaining Review and Delivery Steps
- Final delivery sign-off and git commit/push when authorized by user.
