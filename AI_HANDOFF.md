# AI Handoff & Context Document: Employee Task Manager (`employeetaskmanager`)

## 1. Project Identity and Purpose
- **Project Name:** Employee Task Manager (`employeetaskmanager`)
- **Purpose:** An Android application developed as part of the Android Developer Bootcamp Week 2 (Day 3 REST API Integration & Day 8 Debugging, Bug Reproduction, Fixing & Verification). Provides a robust read-only task dashboard fetching tasks from the public JSONPlaceholder REST API (`GET https://jsonplaceholder.typicode.com/todos`).

## 2. Technology and Package Details
- **Package Name:** `com.example.employee_task_manager`
- **Core Source Directory:** `app/src/main/java/com/example/employee_task_manager/`
- **Target SDK / Min SDK:** Android SDK Platform 35 (min SDK: Android 7.0 / API 24)
- **Language / Toolchain:** Kotlin, Java 11 compatibility, Android Gradle Plugin, Gradle Wrapper (`gradlew`).

## 3. Important Source-Code Paths
- **Activities & UI:**
  - `app/src/main/java/com/example/employee_task_manager/EmployeeTaskManagerActivity.kt`
  - `app/src/main/java/com/example/employee_task_manager/ui/TaskAdapter.kt`
- **Data & Network:**
  - `app/src/main/java/com/example/employee_task_manager/data/Task.kt`
  - `app/src/main/java/com/example/employee_task_manager/data/remote/TodoDto.kt`
  - `app/src/main/java/com/example/employee_task_manager/data/remote/TodoApiService.kt`
  - `app/src/main/java/com/example/employee_task_manager/data/repository/TaskRepository.kt`
  - `app/src/main/java/com/example/employee_task_manager/ui/TaskViewModel.kt`

## 4. What is Currently Implemented (Day 3 & Day 8 Scope Complete)
- Read-only task dashboard rendering tasks fetched via Retrofit from `GET https://jsonplaceholder.typicode.com/todos`.
- MVVM + Repository architecture with `TaskViewModel` exposing `StateFlow<TaskUiState>`.
- DTO mapping with robust validation (`TaskRepository`) preventing Gson reflection null injection crashes.
- Refresh functionality with temporary in-memory task retention during refresh failures and job cancellation race-condition prevention.
- Comprehensive UI state handling: `Loading`, `Success`, `Empty`, `Internet Error`, `Server Error`, `API/Request Error`, and `Invalid Response Error`.
- Day 8 debugging & bug fixes: Malformed/missing/null/blank title handling, HTTP 500 server error handling, request timeout handling (backed by MockWebServer integration tests asserting `SocketTimeoutException`), and D3 refresh flow investigation.
- **Verified Unit Test Execution Results (`app/build/test-results/testDebugUnitTest/`):**
  - `TEST-com.example.employee_task_manager.ExampleUnitTest.xml`: 1 test, 0 failures, 0 errors, 0 skipped.
  - `TEST-com.example.employee_task_manager.ui.TaskViewModelTest.xml`: 16 tests, 0 failures, 0 errors, 0 skipped.
  - **Total:** 17 tests, 0 failures, 0 errors, 0 skipped (`BUILD SUCCESSFUL`).
- **Verified Build Results:**
  - `.\gradlew.bat assembleDebug` → **BUILD SUCCESSFUL in 20s.**

## 5. What is Not Implemented Yet
- Task creation (adding tasks).
- Task completion status editing (marking tasks complete/incomplete).
- Local persistence / Room Database (excluded from scope).

## 6. Current Git Branch and Status
- **Current Git Branch:** `feature/api-task-list`
- **Commit Status:** Uncommitted implementation and debugging changes present in working tree.

## 7. Handoff Snapshot Date
- **Snapshot Date:** November 2026 (Day 8 Complete)

## 8. Completed Work vs. Pending Submission Tasks
- **Completed:** Day 3 GET API integration, Day 8 debugging & bug fixes (Bug 1, Bug 2), unit tests (**17/17 passing unit tests** verified in JUnit XML reports), build verification (`assembleDebug`), evidence collection (`docs/week2/day8/screenshots/`), and documentation updates.
- **Pending Submission:** Final user review and submission of Day 8 work.
