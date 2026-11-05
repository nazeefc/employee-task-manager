# Technical Decisions: Employee Task Manager

## Decision 1: Adoption of MVVM Architecture
- **Context:** The starter project contains a minimal Activity scaffold.
- **Decision:** Adopt Model-View-ViewModel (MVVM) architecture with Repository pattern for future feature implementation.
- **Rationale:** Separation of concerns, testability, lifecycle awareness, and alignment with modern Android development best practices.

## Decision 2: Local Persistence via Room Database
- **Context:** Business requirements require tasks to be viewed, added, and marked complete with persistence across restarts.
- **Decision:** Use Android Room Persistence Library (SQLite abstraction).
- **Rationale:** Type-safe SQL queries, compile-time query verification, coroutine integration (`suspend` functions and `Flow`), and reliable local persistence.

## Decision 3: Strict Adherence to Plan-Before-Coding Protocol (Day 3)
- **Context:** Bootcamp guidelines emphasize planning and documentation before writing implementation code.
- **Decision:** Restrict Day 3 activities strictly to analysis, requirements documentation, architecture planning, and debugging logs.
- **Rationale:** Ensures architectural clarity and stakeholder alignment. Note regarding code state: Git currently shows modifications in the Activity and XML layout compared with HEAD. The exact timing and authorship of these modifications could not be established, so their relationship to Day 3 work remains unverified.
