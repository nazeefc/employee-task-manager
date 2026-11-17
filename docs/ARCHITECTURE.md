# Architecture: Employee Task Manager (Week 3 Day 10 Codex Corrections Complete)

## 1. Current Architecture (Week 3 Offline-First)
- **Activity:** `EmployeeTaskManagerActivity.kt` observes `RoomTaskViewModel` state via lifecycle-safe coroutine collection, hosting a RecyclerView backed by `TaskAdapter`.
- **Data Model:** `data/local/TaskEntity.kt` (`id` Int primary key, `clientId` unique String UUID v4, `title`, `description`, `priority`, `completed`, `updatedAt`, `syncState`) and domain `data/Task.kt`.
- **Database & DAO:** `AppDatabase` (SQLite Room DB version 2 with `MIGRATION_1_2`) and `TaskDao`.
- **Repository (`RoomTaskRepository`):** Coordinates between local Room DAO and domain model mapping, handling offline task creation and status updates with robust `CancellationException` handling.
- **ViewModel (`RoomTaskViewModel`):** Exposes reactive UI state (`RoomTaskUiState`) observing Room database flows with upstream `.catch` error handling.
