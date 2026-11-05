# Architecture: Employee Task Manager

## 1. Current Architecture (As-Is)
Currently, the project consists of a single Activity scaffold:
- **`EmployeeTaskManagerActivity.kt`**: Extends `AppCompatActivity`, loads `employeetaskmanager.xml`, and sets up a simple button click listener displaying a Toast.
- **Storage/Data**: None currently implemented.
- **Architecture Pattern**: Procedural Activity controller (scaffold stage).

---

## 2. Target Architecture (MVVM Clean Architecture - Planned)
To satisfy the business requirements and align with the bootcamp architecture specifications, the application follows a 4-tier layer structure:

### A. SCREEN
- **TaskListScreen:** Renders the task UI.
- **User taps action:** Handles user interactions and input events.

### B. VIEWMODEL
- **State + events:** Manages UI state streams and event handling.
- **Validation:** Validates inputs before processing.

### C. REPOSITORY
- **Business data:** Processes business logic and data models.
- **Read / write:** Coordinates read and write operations between UI/ViewModel and Storage.

### D. STORAGE
- **Local data:** Manages local data models and structures.
- **Future persistence:** Prepared for robust local persistence (e.g., Room Database / SQLite) in future bootcamp phases.

---

## 3. Data Flow

1. **Screen Layer:** User taps action on `TaskListScreen`.
2. **ViewModel Layer:** ViewModel processes events, validates input, and updates state (`StateFlow`).
3. **Repository Layer:** Repository handles business data read/write requests.
4. **Storage Layer:** Storage manages local data and future persistence.

---

## 4. Architecture Diagram

See [architecture.mmd](file:///C:/Users/MAZEEF/AndroidStudioProjects/employeetaskmanager/docs/diagrams/architecture.mmd) for the Mermaid architecture diagram matching the bootcamp specification.
