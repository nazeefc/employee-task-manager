# Technical Decisions: Employee Task Manager

## Decision 1: Adoption of MVVM Architecture
- **Context:** The app uses an Activity hosting a RecyclerView.
- **Decision:** Use MVVM with a Repository pattern for data handling.
- **Rationale:** Separation of concerns, testability, and lifecycle-aware UI state (`StateFlow`).

## Decision 2: In-Memory Data Storage (No Room / No Persistent or Disk Caching)
- **Context:** Scope requires fetching tasks via `GET https://jsonplaceholder.typicode.com/todos`.
- **Decision:** Use temporary in-memory data handling via Repository and ViewModel (`StateFlow`) without Room, SQLite, or persistent/disk caching.
- **Rationale:** Keeps the implementation simple and focused on Retrofit REST API integration and UI state management without unnecessary database complexity.

## Decision 3: GET-Only Scope
- **Context:** Project requirements specify fetching tasks via GET only.
- **Decision:** Exclude Create, Update, and Delete operations.
- **Rationale:** Focus purely on API fetching, JSON parsing, error resilience, and all required UI states.

## Decision 4: Robust DTO-to-Domain Null Validation (Day 8 Bug 1 Fix)
- **Context:** Gson reflection can inject Java `null` into non-nullable Kotlin properties when fields are missing or explicitly null in JSON.
- **Decision:** Add explicit runtime validation during `TaskRepository` DTO mapping to convert reflection nulls into safe `IllegalArgumentException` caught as `Invalid Response Error`.
- **Rationale:** Prevents unhandled `NullPointerException` crashes and provides clear domain error feedback on the UI.
