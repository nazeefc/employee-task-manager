# Development Plan: Employee Task Manager

## 1. Current Scope

Week 2 Day 2 is planning and documentation only. No source code, XML, Gradle configuration, dependencies, or application behavior are changed during this phase.

The confirmed Week 2 Day 3 scope includes both Room Database and REST API integration. Note that while the trainer confirmed API integration and discussed preserving failed operations locally, the project's selected implementation policy is server-first.

## 2. Planned Day 3 Components

- Room `TaskEntity`, `TaskDao`, and `AppDatabase`.
- Retrofit service and the backend-selected JSON converter.
- Repository coordinating REST operations with Room. UI reads from a Room-backed Flow.
- ViewModel exposing `StateFlow<TaskUiState>`.
- Activity/UI integration with lifecycle-safe state collection and operation feedback.
- Explicit mapping between API DTO IDs and the existing Task/Room/domain ID representation after the API schema is available.

## 3. Day 3 Setup and Implementation Sequence

1. Inspect existing Gradle configuration and version catalog for compatibility.
2. Add the compatible Room runtime and compiler setup. Configure Room compiler integration with either KSP or KAPT, after checking project compatibility; choose one compatible approach.
3. Add compatible Retrofit, the selected JSON converter, lifecycle ViewModel/runtime, and coroutine dependencies as needed. Dependency changes are Day 3 work only.
4. Implement Room entity, DAO, and database, then the Retrofit service using the trainer/backend contract when available.
5. Implement Repository read, refresh, create, and completion-toggle behavior:
   - Read tasks from Room Flow.
   - Fetch remote tasks and persist valid successful results to Room.
   - For create and completion toggle, call the API first and update Room only after API success.
   - On operation failure, preserve Room unchanged and report an appropriate error.
   - Reject create/toggle while offline. Do not queue writes or automatically retry.
6. Implement ViewModel states and connect the UI to Room-backed task state and operation errors.

## 4. Planned Verification (Day 3 or scheduled test phase)

- Verify initial loading, empty success, populated Room state, refresh indicators, cached-data warnings, and initial-load errors.
- Verify successful API refresh persists to Room and Room Flow updates the UI.
- Verify failed create and completion toggle leave Room unchanged and show errors.
- Verify offline create/toggle are rejected without Room writes, queueing, or automatic retry.
- Verify response parsing and HTTP/transport error handling.

## 5. Confirmed Decisions and Deferred Backend Details

- **Confirmed:** Room is required as local cache and source for UI reads.
- **Confirmed:** REST API integration is part of Day 3.
- **Confirmed:** Mutations are server-first (trainer confirmed API integration and discussed preserving failed operations locally, but project selection is server-first); Room changes only after API success.
- **Confirmed:** Offline mutations are rejected; queued writes and automatic retries are out of scope.
- **Deferred:** Official backend URL, authentication, exact JSON schema, endpoint paths/payload details, and remote ID format. Do not invent these; use the backend contract when supplied.
- **Completion method:** Use PATCH consistently in the plan.
