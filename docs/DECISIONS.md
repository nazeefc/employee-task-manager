# Technical Decisions: Employee Task Manager

## Decision 1: Adoption of MVVM Architecture
- **Context:** The app uses an Activity hosting a RecyclerView.
- **Decision:** Use MVVM with a Repository pattern for data handling.
- **Rationale:** Separation of concerns, testability, and lifecycle-aware UI state (`StateFlow`).

## Decision 2: In-Memory Data Storage (Week 2 Initial Scope)
- **Context:** Week 2 scope required fetching tasks via `GET https://jsonplaceholder.typicode.com/todos`.
- **Decision:** Used temporary in-memory data handling via Repository and ViewModel (`StateFlow`) without Room or persistent storage during Week 2.
- **Rationale:** Kept Week 2 implementation focused on Retrofit REST API integration and UI state management without premature database complexity.

## Decision 3: GET-Only Scope (Week 2)
- **Context:** Week 2 project requirements specified fetching tasks via GET only.
- **Decision:** Excluded Create, Update, and Delete operations during Week 2.
- **Rationale:** Focused purely on API fetching, JSON parsing, error resilience, and required UI states.

## Decision 4: Robust DTO-to-Domain Null Validation (Day 8 Bug 1 Fix)
- **Context:** Gson reflection can inject Java `null` into non-nullable Kotlin properties when fields are missing or explicitly null in JSON.
- **Decision:** Added explicit runtime validation during `TaskRepository` DTO mapping to convert reflection nulls into safe `IllegalArgumentException` caught as `Invalid Response Error`.
- **Rationale:** Prevented unhandled `NullPointerException` crashes and provided clear domain error feedback on the UI.

## Decision 5: Room Database as Single Source of Truth, Explicit ID Strategy & Non-Destructive Rollback (Week 3 Day 10)
- **Context:** Week 3 introduces offline persistence and local task creation.
- **Decision:** Use Room Database as the single source of truth for the local task list, with `id` (`Int`, auto-generated primary key mapping to domain `Task.id`) and `clientId` (`String`, unique database index storing stable UUID v4). Do not automatically import all 200 JSONPlaceholder todos. Enforce safe conflict handling (`OnConflictStrategy.ABORT`), deliberate updates (`@Update`), and a strict non-destructive downgrade/rollback policy (prohibiting destructive migration fallbacks and silent data deletions).
- **Rationale:** Avoids integer ID collisions, preserves existing Week 2 API tests, ensures offline-first availability, prevents accidental data overwrites, protects user data across app versions, and prepares cleanly for Day 11 WorkManager background sync.
