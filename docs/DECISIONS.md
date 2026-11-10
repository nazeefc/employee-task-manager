# Technical Decisions: Employee Task Manager

## Decision 1: Adoption of MVVM Architecture
- **Context:** The current UI is hosted by an Activity and currently uses in-memory sample data.
- **Decision:** Use MVVM with a Repository for the planned data feature.
- **Rationale:** Separation of concerns, testability, and lifecycle-aware UI state.

## Decision 2: Room Database and REST API
- **Context:** Room was approved by the trainer, and REST API integration is included in Week 2 Day 3.
- **Decision:** Use both Room and the REST API. Room is the local cache and source of task data read by the UI.
- **Rationale:** The Repository exposes Room-backed task data while synchronizing successful remote reads into Room.

## Decision 3: Server-First Mutations
- **Context:** The trainer confirmed the API integration and discussed preserving failed operations locally. The project implementation plan adopts a server-first mutation policy.
- **Decision:** For task creation and completion toggle, call the API first and update Room only after a successful API response. If an operation fails, leave Room unchanged and show an appropriate error.
- **Offline behavior:** Reject task creation and completion toggle while offline. Do not queue offline writes and do not implement automatic retries.
- **Completion endpoint:** Use PATCH consistently in the plan.

## Decision 4: Day 2 / Day 3 Boundary
- **Decision:** Week 2 Day 2 is read-only planning and documentation. Room/API implementation, dependencies, Gradle changes, and application code are Day 3 work.
- **Rationale:** Keep planning reviewable before implementation.

## Decision 5: Backend Contract and ID Mapping
- **Decision:** Do not invent the official API URL, authentication, JSON schema, endpoint payloads, or remote ID format.
- **ID mapping:** When the backend schema is available, explicitly map remote API IDs to the existing Task/Room/domain ID representation; do not assume identical types or values.
