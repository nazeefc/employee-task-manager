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

## Week 3 Day 11 — WorkManager Offline-to-Online Synchronization
- Debug builds use the laptop mock server at `http://127.0.0.1:8080/` through the user's USB reverse setup. The release variant has no configured sync backend; when its sync URL is empty, the worker fails without changing tasks from PENDING or marking them SYNCED.
- **User-reported manual verification:** Offline task creation, Room retention after Force Stop/restart, synchronization after internet restoration, laptop server receipt, distinct client IDs, and repeat POST not increasing server task count were reported PASS by the user. These do not establish timeout-after-save behavior.
- **Build verification:** `:app:assembleDebug` and `:app:assembleDebugAndroidTest` completed successfully during this correction pass.
- **Instrumentation status:** The worker test APK installation on phone `a77e9f7c1122` was blocked by Android with `INSTALL_FAILED_USER_RESTRICTED: Install canceled by user`. `TaskSyncWorkerTest` was not executed in this pass.
- **Device handling:** Existing app was updated in place with `adb install -r`; it was not uninstalled and app data was not cleared.

---

## Week 3 Day 12 Debugging & Verification Log

### **1. Duplicate Launcher Configuration (Implementation Mistake & Correction)**
- **Issue:** Adding `DebugApiTestActivity` with its own launcher intent filter in `app/src/debug/AndroidManifest.xml` created a second app icon in the device launcher.
- **Correction:** Removed the secondary launcher intent filter and restructured `DebugApiTestActivity` as an in-app navigation screen accessible via a button in `EmployeeTaskManagerActivity`, restoring a single launcher icon (`EmployeeTaskManagerActivity`).

### **2. Debug Source Set Deletion (Implementation Mistake & Correction)**
- **Issue:** `app/src/debug/` files were inadvertently removed during restructuring.
- **Correction:** Restored `LocalMockTaskServer.kt` from HEAD baseline and recreated debug cleartext HTTP manifest overlay (`app/src/debug/AndroidManifest.xml`).

---

## Week 3 Day 13 Debugging Investigation Log

### 1. Bug A — Duplicate Task After Retry
- **REPRODUCE:** Attempted controlled reproduction using instrumented test `workerHandlesResponseLossAfterServerSaveAndRetriesIdempotently()` on a physical Xiaomi 22111317I device (Android 14).
- **OBSERVE:** Test ran successfully on physical Xiaomi 22111317I (Android 14) with result **1/1 PASSED**. The mock server received repeated POST requests using the exact same `clientId`. Controlled test verified only one server-side record remained stored.
- **ISOLATE:** Isolated worker retry behavior under dropped response conditions after server persistence.
- **HYPOTHESIZE:** The worker preserves the same `clientId` across retry attempts, and the server prevents duplicate records through unique-key storage using `putIfAbsent(clientId, ...)`. Client-side `clientId` preservation alone does not guarantee idempotency; idempotency relies on both client `clientId` stability and server-side duplicate prevention logic. Note: This hypothesis applies to the tested idempotency implementation; it does not claim that all real-world edge-case retry scenarios are verified.
- **FIX:** Not required — bug not reproduced.
- **VERIFY:** Verified via instrumented test `workerHandlesResponseLossAfterServerSaveAndRetriesIdempotently()` passing 1/1 on physical Xiaomi 22111317I.

### 2. Bug B — Queue Stuck in SYNCING
- **REPRODUCE:** Investigated initial sync failure where background task execution appeared suspended.
- **OBSERVE:**
  - Database Inspector initially showed `syncState = 0` (`PENDING`).
  - Background Task Inspector showed worker status `ENQUEUED` with 5 retries.
  - Worker details showed run count of 6.
  - Logcat showed Worker returning `Result.RETRY`.
  - Network connectivity test `Test-NetConnection 127.0.0.1 -Port 8080` returned `TcpTestSucceeded = False` before starting the mock server.
  - Initial sync failed because the development mock server was not running on laptop port 8080 and phone-to-host port forwarding (`adb reverse`) had not been configured.
  - Environment setup performed:
    - Started existing mock backend using `./gradlew.bat :app:runMockServer`.
    - Connected physical phone port forwarding using `adb reverse tcp:8080 tcp:8080`.
  - After setup, live synchronization reached `syncState = 2` (`SYNCED`).
  - `GET http://127.0.0.1:8080/tasks` confirmed uploaded tasks on the server.
  - Instrumented recovery test `workerResetsStuckSyncingTasksToPending()` executed on physical Xiaomi 22111317I with result **1/1 PASSED**. The test started with `syncState = 1` (`SYNCING`), executed `doWork()`, and verified worker success (`Result.success`) and final state `syncState = 2` (`SYNCED`). The intermediate `PENDING` reset was executed internally during worker startup reset logic (`taskDao.resetSyncingTasksToPending()`) and was not directly verified by a separate test assertion.
- **ISOLATE:** The initial setup lacked a running mock server on port 8080 and ADB reverse forwarding, with the worker `ENQUEUED` with retries while tasks remained `PENDING` (0). After starting `LocalMockTaskServer` and configuring `adb reverse`, live sync reached `SYNCED` (2). This strongly supports an environment configuration problem.
- **HYPOTHESIZE:** The worker returned `Result.RETRY` because network calls to `http://127.0.0.1:8080/` failed due to missing host server and port forwarding, causing WorkManager to re-enqueue retries with `syncState = 0`. However, because the exact original network exception was not captured in logs, a fully proven application-code root cause cannot be claimed.
- **FIX:** Not required — target SYNCING-stuck bug was not reproduced in application code. Development sync connectivity was resolved through environment setup (`runMockServer` and `adb reverse tcp:8080 tcp:8080`), without a source-code fix.
- **VERIFY:** Verified via live physical device sync (`syncState = 2` / `SYNCED`), server GET endpoint response (`GET http://127.0.0.1:8080/tasks`), and instrumented test `workerResetsStuckSyncingTasksToPending()` passing 1/1 on physical Xiaomi 22111317I.

### 3. Bug C — Completion Reverses After Rotation
- **REPRODUCE:** Tested task completion status persistence during screen orientation changes on physical Xiaomi 22111317I.
- **OBSERVE:**
  - Marked a task as Completed on Xiaomi device.
  - Rotated screen orientation from portrait to landscape and back to portrait.
  - Task remained Completed in the UI.
  - Database Inspector confirmed `completed = 1` in Room DB.
  - Mock server later returned record with `title = "task complete rotation test"`, `completed = true`.
- **ISOLATE:** Tested UI state flow and Room database persistence across screen configuration changes.
- **HYPOTHESIZE:** Room DB single source of truth paired with StateFlow cleanly retains and restores task completion state across configuration changes.
- **FIX:** Not required — bug not reproduced in the tested scenario.
- **VERIFY:** Verified manually on physical Xiaomi 22111317I device, Database Inspector, and mock server JSON payload.

### 4. Other Observations
- **Test Installation Permission:** The first instrumented test attempt encountered Android `INSTALL_FAILED_USER_RESTRICTED` due to USB installation restrictions on the Xiaomi device. After installation permission was granted on the device, the relevant tests executed and passed.
- **Mock Server Verification Payload Details:** The mock server recorded two uploaded task records:
  1. `title = "task complete rotation test"`, `completed = true`
  2. `title = "verification"`, `completed = false`
  Both server records contained distinct `clientId` UUIDs.

---

## Actual Codex Agent — Read-Only Root-Cause Investigation

### 1. Bug A — Duplicate Task After Retry Investigation
- **Findings:**
  - `TaskSyncWorker` preserves the same `clientId` UUID across retry attempts when network calls fail or drop responses.
  - Development `LocalMockTaskServer` uses `storedTasks.putIfAbsent(clientId, body)` to enforce unique key storage for incoming POST payloads.
  - No incorrect state transition or duplicate task generation was demonstrated in controlled testing or code review.
  - Note: Production backend idempotency behavior remains unverified on live remote endpoints.

### 2. Bug B — Queue Stuck in SYNCING Investigation
- **Findings:**
  - `TaskSyncWorker` contains recovery logic upon startup (`taskDao.resetSyncingTasksToPending()`) that resets interrupted `SYNCING` tasks (`syncState = 1`) back to `PENDING` (`syncState = 0`).
  - Initial observations of `PENDING` state (`syncState = 0`) and worker `Result.RETRY` were consistent with an unavailable server connection.
  - Background synchronization succeeded (`syncState = 2` / `SYNCED`) after starting `LocalMockTaskServer` and configuring USB port forwarding (`adb reverse tcp:8080 tcp:8080`).
  - No `SYNCING`-stuck defect was confirmed in application code.
  - Note: The exact original network exception was not captured in log records.

### 3. Bug C — Completion Reverses After Rotation Investigation
- **Findings:**
  - Room database (as single source of truth) paired with ViewModel `StateFlow` retains UI state seamlessly across screen orientation changes.
  - Task completion status remained `true` in UI, Room DB (`completed = 1`), and mock server upload (`completed = true`) after rotation.
  - No completion reversal was demonstrated in the tested scenario.

### 4. Separate Follow-Up Risk (Post-Sync Completion Changes)
- **Observation:** `RoomTaskRepository.toggleTaskCompletion()` updates the local Room database (`updateTask`), but does not enqueue a WorkManager synchronization request.
- **Risk:** An already-`SYNCED` task (`syncState = 2`) that has its completion status toggled locally might not trigger a new sync request to upload the updated completion state to the remote server.
- **Clarification:** This is a separate post-sync update limitation, NOT the reported rotation bug.
- **Action Required:** Requires separate verification and user approval before implementing any code changes.

### 5. Codex Final Recommendations
- **Source Code Changes:** No source-code modifications required for Bugs A, B, or C.
- **Regression Tests:** Preserve all existing JVM unit tests and instrumented worker tests.
- **Future Test Considerations:** Consider adding future test coverage for production backend idempotency, server outage recovery, Activity recreation, and completion state synchronization after initial task upload.