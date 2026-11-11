# Development Plan: Employee Task Manager (Day 3 Implementation)

## 1. Current Scope
- **Broader Business Goal:** Employees should be able to view tasks, add tasks, mark tasks complete, and see their status.
- **Approved Day 3 Implementation Scope:** **GET task list only** (`GET https://jsonplaceholder.typicode.com/todos`). Read-only task dashboard; task creation and completion editing are not implemented.
- In-memory data only. No Room, no SQLite, no local database, no persistent storage, no disk caching, no offline synchronization.
- No Create, Update, or Delete (CUD) mutations in Day 3.

## 2. Implemented Components
- **Remote DTO & API Service:** `TodoDto`, `TodoApiService` using Retrofit and Gson converter.
- **Repository:** `TaskRepository` executing API calls and mapping DTOs to domain `Task` models where API `id` → `Task.id`, API `title` → `Task.title`, API `completed` → `Task.isCompleted`, and `userId` + `id` generate the task description (`"Task ID: ${dto.id} • User ID: ${dto.userId}"`).
- **ViewModel:** `TaskViewModel` managing `TaskUiState` (`Loading`, `Success`, `Empty`, and `Error(message)` carrying classified error messages for internet, server, API/request, and invalid response errors) and retaining in-memory tasks during refresh failures with race-condition job cancellation.
- **UI & Activity:** `EmployeeTaskManagerActivity` observing `StateFlow` lifecycle-safely and updating `TaskAdapter`.

## 3. Verification Plan & Selected Test Coverage
- Unit tests (`TaskViewModelTest` with 10 test methods covering DTO mapping, empty response, internet error, separate `IllegalArgumentException` invalid-response classification, actual Retrofit + Gson malformed JSON response, race-condition suppression, cancellation re-throw, refresh cache retention, HTTP 4xx, and HTTP 5xx, plus `ExampleUnitTest` with 1 test method, totaling 11 test methods).
- Build verification via `./gradlew.bat assembleDebug`.
