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

### 3. Refresh Tasks Button Does Nothing (Day 4 - Verified)
- **Observed Symptom:** Tapping the Refresh Tasks button initially produced no action.
- **Root Cause:** The button had no click listener.
- **Fix:** Added `findViewById<Button>(R.id.buttonRefreshTasks)` and a `setOnClickListener` that reassigns the RecyclerView adapter using the existing sample task list.
- **Verification:** Build succeeded. A breakpoint inside the click listener was hit when the button was tapped. App did not crash.
- **Limitation:** This reloads the same sample data; it does not fetch new data from a backend.

### 4. Task Completion Status Displayed Incorrectly (Day 4 - Verified)
- **Observed Symptom:** Completed tasks appeared as Pending, and Pending tasks appeared as Completed.
- **Root Cause:** The status labels and colors were assigned to the wrong Boolean branches in `TaskAdapter.kt`.
- **Fix:** Corrected the mapping: `isCompleted = true` displays Completed in green; `false` displays Pending in orange.
- **Verification:** Rebuilt and ran the app. The user visually confirmed that the statuses now display correctly.

> [!NOTE]
> These were controlled debugging practice exercises, not confirmed trainer-provided bugs.
