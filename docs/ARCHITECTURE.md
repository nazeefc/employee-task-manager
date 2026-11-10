# Architecture: Employee Task Manager

## 1. Current Architecture (As-Is)

- **Activity:** `EmployeeTaskManagerActivity.kt` extends `AppCompatActivity`, applies window insets, and hosts a RecyclerView backed by `TaskAdapter`.
- **Data:** `data/Task.kt` is the current task model. The Activity creates four static sample tasks in memory.
- **Interaction:** The Refresh Tasks button reuses the same sample list.
- **Not yet implemented:** ViewModel, Repository, Room database, and REST API integration.

## 2. Confirmed Target Architecture

Week 2 Day 3 is planned to integrate **both Room and a REST API**. Room is the local cache and the source read by the UI. The Repository coordinates remote operations and local persistence.

### Screen / View
- Renders immutable task UI state and forwards add, completion-toggle, and refresh actions.
- Collects the ViewModel state lifecycle-safely.

### ViewModel
- Exposes read-only `StateFlow<TaskUiState>`.
- Runs work in `viewModelScope`, validates user input, and translates Repository outcomes into UI state.

### Repository
- Exposes a Room DAO-backed `Flow` of domain tasks for UI reading.
- Refreshes from the REST API and saves successful responses into Room; Room Flow emissions update the UI.
- **Mutation Policy Clarification:**
  - Trainer confirmed API integration and discussed preserving failed operations locally.
  - The project's selected implementation policy is server-first.
  - API must succeed before Room is updated.
  - On API failure, Room remains unchanged and an error is shown.
  - Offline create and completion toggle are rejected.
  - No offline write queue or automatic retry.

### Data models and ID mapping
- Keep separate Room entity, API DTO, and domain `Task` models with explicit mapping functions.
- The existing `Task` model uses an integer ID. When the API schema is available, explicitly map remote API IDs to the existing Task/Room/domain ID representation. Do not assume the remote ID type or value is identical.

## 3. UI States and Room-Backed Read Flow

The ViewModel must distinguish an empty Room cache during the first fetch from a confirmed empty task list.

- **InitialLoading:** No cached tasks exist and the first network fetch is still pending. A zero-row Room emission alone must not show Empty.
- **Success(tasks):** Room contains tasks. During refresh, retain these tasks and set a refresh indicator.
- **Success with warning:** If refresh fails while cached tasks exist, keep displaying Room tasks and show a non-blocking sync/offline warning.
- **Empty:** Show only after the first network fetch succeeds with an empty result, that empty result is saved/synchronized to Room, and the Room Flow emits an empty list.
- **Error:** If the initial fetch fails and Room has no cached tasks, show an error with a retry action. Automatic retry is not required.

Mutation failures are separate from task-list read state: show an appropriate operation error while retaining the existing Room-backed task list unchanged.

## 4. Error Classification

Classify `SocketTimeoutException` before generic `IOException`, because it is a subtype of `IOException`.

- **Timeout:** Report a timeout-specific message. For reads, keep cached Room data if available; otherwise show the initial-load error.
- **Other transport/network failure:** For reads, keep cached data with a warning or show the initial-load error when no cache exists.
- **HTTP 4xx:** Treat as an API client/request failure and show an appropriate error. For a mutation, do not write to Room.
- **HTTP 5xx:** Treat as a server failure and show an appropriate error. For a mutation, do not write to Room.
- **Invalid/unparseable response:** Treat as a response parsing failure; do not persist an invalid result. Preserve cached read data when available.
- **Invalid task title:** Validate locally, do not call the API, and show the field validation message.

For every failed create or completion toggle, preserve Room unchanged. Offline create and toggle operations are rejected; do not queue them or retry automatically.

## 5. API Planning Boundary

The API integration is confirmed for Day 3, but no official base URL, authentication method, endpoint schema, payload fields, or remote ID format is specified here. These remain placeholders for the backend specification. Use **PATCH** consistently for the completion endpoint once its path and payload are provided.

## 6. Diagrams

- [Architecture diagram](diagrams/architecture.mmd) shows Screen/ViewModel/Repository, REST API, Room, and the Room-backed read path.
- [Sequence diagram](diagrams/sequence.mmd) covers initial loading, empty success, refresh failure, successful and failed mutations, and offline mutation rejection.
