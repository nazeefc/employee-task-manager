# Architecture: Employee Task Manager

## 1. Current Architecture (As-Is)
Currently, the project consists of a single Activity scaffold:
- **`EmployeeTaskManagerActivity.kt`**: Extends `AppCompatActivity`, loads `employeetaskmanager.xml`, and sets up a simple button click listener displaying a Toast.
- **Storage/Data**: None currently implemented.
- **Architecture Pattern**: Procedural Activity controller (scaffold stage).

---

## 2. Target Architecture (MVVM Clean Architecture - Planned)
To satisfy the business requirements ("Employees must view tasks, add tasks, mark complete, and see status"), the project will adopt the standard Android MVVM (Model-View-ViewModel) architecture:

### A. Screen / UI Layer
- **Activities / Fragments / Adapters (or Jetpack Compose):** Display the task list, render task items with status indicators, and handle user input (adding tasks, toggling completion).
- Observes reactive streams (`StateFlow` / `LiveData`) exposed by the ViewModel.

### B. ViewModel Layer
- **`TaskViewModel`:** Holds and manages UI-related data in a lifecycle-conscious way.
- Exposes state (`StateFlow<TaskUiState>`) to the UI layer and handles user actions (e.g., `addTask()`, `toggleTaskComplete()`).

### C. Repository Layer
- **`TaskRepository`:** Single source of truth for task data operations.
- Abstracts data sources (Room database vs. network/mock sources).

### D. Storage / Database Layer
- **Room Database (`TaskDao`, `TaskEntity`, `AppDatabase`):** Local SQLite persistence ensuring tasks survive app restarts.

---

## 3. Data Flow

1. **User Interaction:** User taps "Add Task" or toggles task completion in the UI (`EmployeeTaskManagerActivity` / UI components).
2. **ViewModel Action:** UI invokes a method on `TaskViewModel`.
3. **Repository Operation:** `TaskViewModel` calls `TaskRepository` to insert or update the task.
4. **Database Persistence:** `TaskRepository` executes the query via `TaskDao` on a background dispatcher (Kotlin Coroutines).
5. **State Emission:** Updated task list flows back from the database through `TaskDao` -> `TaskRepository` -> `TaskViewModel` (`StateFlow`), which automatically updates the UI.

---

## 4. Architecture Diagram

See [architecture.mmd](file:///C:/Users/MAZEEF/AndroidStudioProjects/employeetaskmanager/docs/diagrams/architecture.mmd) for the Mermaid sequence/component diagram.
