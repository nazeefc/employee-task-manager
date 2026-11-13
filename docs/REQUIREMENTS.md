# Requirements: Employee Task Manager (Day 3 GET Implementation)

## Business Requirement & Scope Context

### Broader Business Goal
> "Employees must view tasks, add tasks, mark tasks complete, and see their status."

### Approved Day 3 Implementation Scope
The current implementation is limited to fetching tasks through the JSONPlaceholder GET API (`GET https://jsonplaceholder.typicode.com/todos`), displaying task information and completion status, supporting refresh, and showing loading, empty, and error states.
- **Adding tasks** is not implemented in Day 3.
- **Editing completion status** is not implemented in Day 3.

---

## 1. User Stories

| ID | As a(n)... | I want to... | So that... |
|---|---|---|---|
| **US-01** | Employee | view a list of tasks fetched from the API | I know what work needs to be done. |
| **US-02** | Employee | refresh the task list via a Refresh button | I can fetch the latest updates from the API. |
| **US-03** | Employee | see each task's Pending or Completed status | I can distinguish finished work from unfinished work. |
| **US-04** | Employee | see loading, success, empty, internet error, server error, API/Request error, invalid response error, and refresh warning feedback | I understand the current data fetching state. |

---

## 2. Functional Requirements

- **FR-01 (View Tasks):** Display tasks showing each task's title, generated description (task ID and user ID), and completion status.
- **FR-02 (Fetch API):** Fetch task items from `GET https://jsonplaceholder.typicode.com/todos` using Retrofit.
- **FR-03 (Refresh):** Provide a Refresh button that fetches API data again, temporarily retaining previously loaded tasks in ViewModel memory during refresh failures.
- **FR-04 (UI States):** Handle Loading, Success, Empty ("No tasks found"), Internet Error, Server Error, API/Request Error, Invalid Response Error, and refresh warning while retaining previous tasks.

---

## 3. Acceptance Criteria

- **AC-01 (Success):** Display tasks successfully in the RecyclerView when the API returns data.
- **AC-02 (Empty):** Show "No tasks found" when the API succeeds but returns an empty list.
- **AC-03 (Server Error):** Show an appropriate error message for unsuccessful 5xx server HTTP responses.
- **AC-04 (API/Request Error):** Show an appropriate error message for unsuccessful 4xx client HTTP responses.
- **AC-05 (Internet Error):** Show an appropriate message when the network is unavailable or request fails due to connectivity (`IOException`).
- **AC-06 (Invalid Response Error):** Show an appropriate message for JSON parsing or conversion failures.
- **AC-07 (Refresh Failure):** Keep previously loaded tasks temporarily visible in memory if a refresh request fails, and show the error message separately.

---

## 4. API Contract

### 1. API Details
- **Provider:** JSONPlaceholder
- **Base URL:** `https://jsonplaceholder.typicode.com/`
- **HTTP Method:** `GET`
- **Endpoint:** `/todos`
- **Full URL:** `https://jsonplaceholder.typicode.com/todos`
- **Authentication:** None required.

### 2. Response Structure
- The API returns a JSON array containing todo item objects.
- Each item in the array contains:
  - `userId`: Integer
  - `id`: Integer
  - `title`: String
  - `completed`: Boolean
- **Illustrative JSON Response Example:**
  ```json
  [
    {
      "userId": 1,
      "id": 1,
      "title": "delectus aut autem",
      "completed": false
    },
    {
      "userId": 1,
      "id": 2,
      "title": "quis ut nam facilis et officia qui",
      "completed": false
    }
  ]
  ```

### 3. DTO to Domain Model Mapping
- **`TodoDto` to `Task` Mapping (`TaskRepository.kt`):**
  - `dto.id` → `Task.id`
  - `dto.title` → `Task.title` (subject to runtime validation)
  - `dto.completed` → `Task.isCompleted`
  - `dto.userId` and `dto.id` are combined to generate the task description: `"Task ID: ${dto.id} • User ID: ${dto.userId}"`.
- **Title Validation & Error Resilience:**
  - `TaskRepository` validates `dto.title` to ensure it is present, non-null, and non-blank (`dto.title?.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException(...)`).
  - Missing, explicit `null`, or blank/whitespace-only titles throw an `IllegalArgumentException`, which is caught and mapped by the repository as `Result.Error`, presenting an `Invalid Response Error` on the UI.

### 4. API Response and Error Handling
- **Successful Response:** Returns `Result.Success(tasks)` which populates the RecyclerView and transitions state to `TaskUiState.Success`.
- **Successful Empty Response (`[]`):** Returns an empty list, transitioning state to `TaskUiState.Empty` (`"No tasks found"`).
- **HTTP 4xx Errors:** Client-side request failures (`code in 400..499`) are caught as `HttpException` and classified as `API/Request Error (4xx)`.
- **HTTP 5xx Errors:** Server-side failures (`code in 500..599`) are caught as `HttpException` and classified as `Server Error (5xx)`.
- **Network Failures & Timeouts:** Connectivity issues and read/connect timeouts (`IOException`, `SocketTimeoutException`) are caught and classified as `Internet Error: Network connectivity unavailable or request timed out.`
- **Malformed JSON / Invalid Responses:** GSON parser exceptions (`JsonSyntaxException`) and validation exceptions are caught and classified as `Invalid Response Error`.
