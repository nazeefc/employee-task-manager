# Technical Decisions: Employee Task Manager

## Decision 1: Adoption of MVVM Architecture
- **Context:** The app uses an Activity hosting a RecyclerView.
- **Decision:** Use MVVM with a Repository pattern for data handling.
- **Rationale:** Separation of concerns, testability, and lifecycle-aware UI state (`StateFlow`).

## Decision 2: In-Memory Data Storage (No Room / No Persistent or Disk Caching)
- **Context:** Day 3 requires fetching tasks via `GET https://jsonplaceholder.typicode.com/todos`.
- **Decision:** Use temporary in-memory data handling via Repository and ViewModel (`StateFlow`) without Room, SQLite, or persistent/disk caching.
- **Rationale:** Keeps the Day 3 implementation simple and focused on Retrofit REST API integration and UI state management without unnecessary database complexity.

## Decision 3: GET-Only Scope for Day 3
- **Context:** Day 3 requirements specify fetching tasks via GET only.
- **Decision:** Exclude Create, Update, and Delete operations for Day 3.
- **Rationale:** Focus purely on API fetching, JSON parsing, and all required UI states (Loading, Success, Empty, Internet Error, Server Error, API/Request Error, Invalid Response Error, and refresh warning retention).
