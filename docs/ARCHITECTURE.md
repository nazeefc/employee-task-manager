# Architecture: Employee Task Manager (Week 3 Day 11 Complete)

## 1. Offline-First Synchronization Architecture
- **Room Database (v2):** Acts as the local single source of truth for task creation, viewing, and state tracking (`syncState`: 0 = PENDING, 1 = SYNCING, 2 = SYNCED).
- **WorkManager (`TaskSyncWorker`):** Background task worker executing under `NetworkType.CONNECTED` constraints with exponential backoff.
- **Repository (`RoomTaskRepository`):** Coordinates local Room DAO persistence and automatically enqueues WorkManager background synchronization when new tasks are created.
- **Network / Remote (`TaskSyncApiService`):** Retrofit POST service sending idempotent task payloads identified by stable `clientId`.
