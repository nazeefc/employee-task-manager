# Architecture: Employee Task Manager (Day 3 GET Implementation)

## 1. Current Architecture
- **Activity:** `EmployeeTaskManagerActivity.kt` extends `AppCompatActivity`, applies window insets, observes ViewModel `StateFlow` lifecycle-safely, and hosts a RecyclerView backed by `TaskAdapter`.
- **Data Model:** `data/Task.kt` holds `id`, `title`, `description` (generated from task ID and user ID), and `isCompleted`.
- **Network / Repository:** Retrofit API (`TodoApiService`) fetching `GET https://jsonplaceholder.typicode.com/todos`, parsed via Gson into `TodoDto`, and mapped by `TaskRepository` to domain `Task` models in memory.
- **Scope:** GET task list only. No local database (no Room, no SQLite), no persistent or disk caching, and no mutations (CUD).

## 2. Target Architecture (MVVM + Repository + Retrofit - In-Memory)

### Screen / View
- Observes `TaskViewModel.uiState` lifecycle-safely (`repeatOnLifecycle`).
- Renders loading, success (with optional refresh warning), empty, and error states.
- Forwards refresh actions to the ViewModel.

### ViewModel (`TaskViewModel`)
- Exposes read-only `StateFlow<TaskUiState>`.
- Manages temporary in-memory task caching for the current app process.
- Handles error classification (Internet Error, Server Error, API/Request Error, Invalid Response Error).
- Retains previously loaded tasks temporarily in memory during refresh failures, preventing race conditions via job cancellation.

### Repository (`TaskRepository`)
- Coordinates network requests to `TodoApiService`.
- Maps `TodoDto` items to domain `Task` models:
  - API `id` → `Task.id`
  - API `title` → `Task.title`
  - API `completed` → `Task.isCompleted`
  - API `userId` and `id` are combined to generate the task description (`"Task ID: ${dto.id} • User ID: ${dto.userId}"`).
- Exposes `Result<List<Task>>` to the ViewModel, rethrowing `CancellationException` to preserve coroutine cancellation contract.

## 3. Implemented UI States

- **Loading:** Shown during initial fetch.
- **Success:** Displays task list in RecyclerView.
- **Success with Refresh Warning:** If a background refresh fails while cached tasks exist in ViewModel memory, previously loaded tasks remain visible and a warning message is shown separately.
- **Empty:** Shown when API succeeds but returns an empty list (`[]`).
- **Internet Error:** Shown when network is unavailable or request fails due to connectivity (`IOException`).
- **Server Error:** Shown for unsuccessful 5xx server HTTP responses.
- **API/Request Error:** Shown for unsuccessful 4xx client HTTP responses.
- **Invalid Response Error:** Shown for JSON parsing or conversion failures.

## 4. Diagrams

- [Architecture diagram](diagrams/architecture.mmd)
- [Sequence diagram](diagrams/sequence.mmd)
