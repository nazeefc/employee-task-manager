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
