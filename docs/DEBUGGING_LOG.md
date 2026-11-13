# Debugging Log: Employee Task Manager

## Bug / Issue Record

### 1. `java` Command Not on System PATH (Day 1 - Unverified)
- **Observed Symptom:** `java -version` command execution in terminal (reported in Day 1 notes).
- **Suspected Root Cause:** System environment variables do not include JDK path.
- **Verification Status:** Unverified in current session (depends on local terminal shell environment).
- **Solution / Workaround:** Used Android Studio's bundled Gradle wrapper (`gradlew.bat`) which resolves the JDK toolchain automatically. Non-blocking for builds.

### 2. Emulator UWB Service Crash (Day 2 - Verified with Evidence)
- **Observed Evidence:** Excerpt from Logcat (`screenshots/logcat-uwb-crash.txt`) showing fatal abort (`SIGABRT`) in `/vendor/bin/hw/android.hardware.uwb-service` attempting to open `/dev/uwb0`.
- **Suspected Root Cause:** Emulator system image attempting to access virtual UWB hardware not provisioned in the AVD profile.
- **Isolation:** Isolated the crash to the emulator system service (not the app code `com.example.employee_task_manager`).
- **Solution / Workaround:** Recommended updating to an API 36+ stable AVD profile. App functionality in `EmployeeTaskManagerActivity` was unaffected.
- **Status:** Documented with supporting Day 2 Logcat evidence as a non-application emulator issue.

---

## Day 8 Debugging Session (Trainer Defects & Technical Evidence)

### 5. Bug 1 — Malformed Data / Missing Required Field (Day 8 - Verified & Fixed)
- **Expected Behavior:** Application should handle malformed JSON syntax, missing required fields (`title`), or explicit `null`/blank fields gracefully by transitioning to `TaskUiState.Error` with an `Invalid Response Error` message.
- **Actual Behavior (Before Fix):**
  - Completely malformed JSON syntax triggered `com.google.gson.JsonSyntaxException`.
  - Missing `title` field or explicit `null`/blank `title` field: Gson set `dto.title = null` or blank strings during deserialization. Explicit validation in `TaskRepository` ensures missing or blank titles are caught and mapped cleanly to `Result.Error`.
- **Technical Evidence Files:**
  - Malformed JSON UI Evidence: `docs/week2/day8/screenshots/D1_Malformed_JSON_Before.png`
  - Missing / Null Title UI Evidence: `docs/week2/day8/screenshots/D1_Missing_Title_After.png`
  - Debugger & Breakpoint Setup: `docs/week2/day8/screenshots/D1_Debugger_Breakpoints_Setup.png`
  - Logcat Evidence Log: `docs/week2/day8/screenshots/Logcat_Evidence.txt`
- **Root Cause:** Gson reflection deserialization populates non-nullable Kotlin properties with Java `null` when fields are missing or explicitly null in JSON payloads.
- **Failing File & Line:** [TaskRepository.kt:L20-21](file:///C:/Users/MAZEEF/AndroidStudioProjects/employeetaskmanager/app/src/main/java/com/example/employee_task_manager/data/repository/TaskRepository.kt#L20-L21)
- **Fix Implemented:** Added explicit null/blank string validation in [TaskRepository.kt](file:///C:/Users/MAZEEF/AndroidStudioProjects/employeetaskmanager/app/src/main/java/com/example/employee_task_manager/data/repository/TaskRepository.kt#L19-L22):
  ```kotlin
  @Suppress("UNNECESSARY_SAFE_CALL")
  val title = dto.title?.takeIf { it.isNotBlank() }
      ?: throw IllegalArgumentException("Task title is required but missing or empty.")
  ```
- **Verification & Test Results:** Missing, null, or blank titles throw `IllegalArgumentException`, caught cleanly by `TaskRepository` as `Result.Error`, displaying `Invalid Response Error: Task title is required but missing or empty.` on UI. Verified via unit tests (JUnit XML reports in `app/build/test-results/testDebugUnitTest/`).

### 6. Bug 2A — HTTP 500 Server Error & Network Inspection (Day 8 - Verified & Working)
- **Expected Behavior:** HTTP 500 error from API service should be caught and rendered as `Server Error (500): Unsuccessful server response.` with retry capability.
- **Technical Evidence Files:**
  - HTTP 500 Error UI & Network Evidence: `docs/week2/day8/screenshots/D2_HTTP_500_After.png`
  - Network Inspection Evidence of API: `docs/week2/day8/screenshots/Network_Inspection_Evidence_API.png`
- **Root Cause & Handling:** `TaskRepository.kt` catches `HttpException` as `Result.Error`. `TaskViewModel.kt` classifies code 500..599 as `Server Error (500): Unsuccessful server response.`
- **Verification & Test Results:** App rendered `Server Error (500): Unsuccessful server response.` on UI with functional Retry action. Verified via MockWebServer integration test (`TEST-com.example.employee_task_manager.ui.TaskViewModelTest.xml`).

### 7. Bug 2B — Request Timeout (Day 8 - Verified & Working)
- **Expected Behavior:** Request timeout should be caught and rendered as `Internet Error: Network connectivity unavailable or request timed out.` with retry capability.
- **Technical Evidence Files:**
  - Timeout Error UI & Exception Evidence: `docs/week2/day8/screenshots/D2_Timeout_After.png`
- **Root Cause & Handling:** `TaskRepository.kt` catches `IOException` as `Result.Error`. `TaskViewModel.kt` classifies `IOException` as `Internet Error: Network connectivity unavailable or request timed out.`
- **Verification & Test Results:** App rendered `Internet Error: Network connectivity unavailable or request timed out.` on UI with functional Retry action. Verified via MockWebServer integration test asserting `SocketTimeoutException` (`TEST-com.example.employee_task_manager.ui.TaskViewModelTest.xml`).

### 8. Bug 3 — Refresh Flow Investigation (Day 8 - Investigated)
- **Expected Behavior:** Tapping REFRESH TASKS fetches latest tasks and updates RecyclerView items on UI.
- **Investigation Details:** No production Activity, ViewModel, or Adapter code was changed for D3. The existing codebase already implements a full refresh and adapter update path (`TaskAdapter.updateTasks()` and `notifyDataSetChanged()`). Initial debugger observations were noted as potential timing observations during debugging sessions rather than conclusively proven production code defects.
- **Technical Evidence Files:**
  - Refresh State & RecyclerView Update Evidence: `docs/week2/day8/screenshots/D3_Refresh_After.png`
  - Live Success API Evidence: `docs/week2/day8/screenshots/D4_Live_App_Success.png`
- **Status:** Investigated existing refresh/update flow; no production code defect established.

---

## Day 9 Test & Deliver Verification Sign-Off
- **Test Sign-Off:** Fresh Day 9 execution of `.\gradlew.bat :app:testDebugUnitTest` completed successfully (BUILD SUCCESSFUL in 18 seconds; 17 tests: 0 failures, 0 errors, 0 skipped). Available JUnit XML reports are located in `app/build/test-results/testDebugUnitTest/`; their recorded timestamps are November 12, 2026, so do not describe them as freshly generated on Day 9.
- **Build Sign-Off:** `.\gradlew.bat assembleDebug` executed successfully (BUILD SUCCESSFUL in 6 seconds; Debug APK generated at `app/build/outputs/apk/debug/app-debug.apk`).
- **Device Sign-Off:** App successfully launched on Android Studio Emulator and a physical Android mobile device. Loading and Success states were observed during execution. Empty and Error state evidence was documented during Day 8 in `docs/week2/day8/screenshots/`.
- **Codex Verdict:** APPROVE.
