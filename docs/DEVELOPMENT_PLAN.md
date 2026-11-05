# Development Plan: Employee Task Manager

## 1. Existing Files That May Need Modification
- **`app/build.gradle.kts`**: Add dependencies for Room, Lifecycle ViewModel, RecyclerView, Coroutines (if not already present).
- **`app/src/main/AndroidManifest.xml`**: Register any new Activities or services if required (or keep single Activity with Fragments/Views).
- **`app/src/main/java/com/example/employee_task_manager/EmployeeTaskManagerActivity.kt`**: Expand from toast scaffold to host task views and observe `TaskViewModel`.
- **`app/src/main/res/layout/employeetaskmanager.xml`**: Replace or update layout with RecyclerView for tasks and input controls for adding tasks.

---

## 2. New Application Files Required (Planned)
- **Model:** `com/example/employee_task_manager/data/Task.kt` (Entity data class).
- **Database / DAO:**
  - `com/example/employee_task_manager/data/TaskDao.kt`
  - `com/example/employee_task_manager/data/AppDatabase.kt`
- **Repository:** `com/example/employee_task_manager/data/TaskRepository.kt`
- **ViewModel:** `com/example/employee_task_manager/ui/TaskViewModel.kt`
- **UI / Adapter:** `com/example/employee_task_manager/ui/TaskAdapter.kt` (or Compose equivalents if Compose is chosen).

---

## 3. Step-by-Step Implementation Strategy
1. **Step 1: Dependencies & Setup** — Add Room, Lifecycle, and Coroutine dependencies in `app/build.gradle.kts` and sync Gradle.
2. **Step 2: Data Model & Database** — Implement `Task` entity, `TaskDao`, and `AppDatabase` with Room.
3. **Step 3: Repository** — Implement `TaskRepository` wrapping `TaskDao` operations.
4. **Step 4: ViewModel** — Implement `TaskViewModel` exposing task state via `StateFlow`.
5. **Step 5: UI Integration** — Update `EmployeeTaskManagerActivity` and XML layouts (or fragments/adapters) to display the task list, add new tasks, and toggle task completion.
6. **Step 6: Verification & Testing** — Run unit tests and instrumented UI/DB tests.

---

## 4. Testing Plan
- **Unit Tests:** Test `TaskViewModel` and `TaskRepository` logic using JUnit and Turbine (for Flow testing).
- **Instrumented Tests:** Test Room database DAO queries (`TaskDaoTest`) and UI interactions (`EmployeeTaskManagerActivityTest` with Espresso).

---

## 5. Potential Technical Risks & Assumptions
- **Assumption:** SQLite/Room local persistence is sufficient (no backend synchronization required for initial bootcamp requirements).
- **Risk:** Gradle sync issues or dependency version mismatches when adding Room and Lifecycle libraries; mitigated by pinning compatible stable library versions in `libs.versions.toml`.
