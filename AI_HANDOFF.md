# AI Handoff & Context Document: Employee Task Manager (`employeetaskmanager`)

## 1. Project Identity and Purpose
- **Project Name:** Employee Task Manager (`employeetaskmanager`)
- **Purpose:** An Android application developed as part of the Android Developer Bootcamp. The current Day 3 implementation provides a read-only task dashboard fetching tasks from the public JSONPlaceholder REST API (`GET https://jsonplaceholder.typicode.com/todos`).

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

## 4. What is Currently Implemented (Day 3 Scope)
- Read-only task dashboard rendering tasks fetched via Retrofit from `GET https://jsonplaceholder.typicode.com/todos`.
- MVVM + Repository architecture with `TaskViewModel` exposing `StateFlow<TaskUiState>`.
- DTO mapping where API `id` maps to `Task.id`, API `title` maps to `Task.title`, API `completed` maps to `Task.isCompleted`, and API `userId` is used alongside `id` to generate the task description (`"Task ID: ${dto.id} • User ID: ${dto.userId}"`).
- Refresh functionality with temporary in-memory task retention during refresh failures and job cancellation race-condition prevention.
- Comprehensive UI state handling: `Loading`, `Success`, `Empty`, `Internet Error`, `Server Error`, `API/Request Error`, and `Invalid Response Error`.
- Unit tests (`TaskViewModelTest.kt` with 10 test methods, plus `ExampleUnitTest.kt` with 1 test method, totaling 11 test methods).

## 5. What is Not Implemented Yet
- Task creation (adding tasks).
- Task completion status editing (marking tasks complete/incomplete).
- Local persistence / Room Database (excluded from approved Day 3 scope).

## 6. Current Git Branch and Status
- **Current Git Branch:** `feature/api-task-list`
- **Commit Status:** Uncommitted implementation changes present in working tree.

## 7. Handoff Snapshot Date
- **Snapshot Date:** 11 November 2026

## 8. Completed Work vs. Pending Submission Tasks
- **Completed:** Day 3 GET API integration, ViewModel, Repository, Retrofit client, unit tests, and documentation updates.
- **Pending Submission:** Final user review and submission of Day 3 work.
