# Day 2: Crash Analysis

## Summary

The supplied Logcat entry is a crash in the Android Emulator's UWB system service, not in Employee Task Manager.

## Expected and actual behavior

- **Expected:** The app launches and remains available for interaction.
- **Actual:** Logcat reports a fatal abort from the emulator's UWB service. The app build/run event reports success.

## Evidence

The crash entry identifies:

- Executable: `/vendor/bin/hw/android.hardware.uwb-service`
- Command line: `/vendor/bin/hw/android.hardware.uwb-service /dev/uwb0`
- Signal: `SIGABRT`
- Abort message: `failed to open the serial device: ... No such device or address`

## Isolation and root cause

The failing process is `android.hardware.uwb-service`, not `com.example.employee_task_manager`. The emulator's UWB service attempts to open `/dev/uwb0`, but the virtual device does not provide that serial device. The service aborts. This is an emulator/system-image issue and does not identify a fault in the app's Activity or layout.

## Verification and resolution

The IDE reports the app build/run as successful. The previous device check also showed `EmployeeTaskManagerActivity` in the foreground while the UWB service emitted its crash.

To avoid this system-service crash during app work, use an updated stable emulator system image (for example Android 16 / API 36) in a new AVD, then run the app there. Keep the supplied UWB crash log as environment evidence; if the service still crashes on the new image, report it separately as an emulator issue.

## Evidence files

- `screenshots/app-click-flow.png`: app screen with the button visible.
- `screenshots/breakpoint-click-listener.png`: Android Studio debugger view on the button listener's Toast line, with the click listener stack and button variable visible.
- `screenshots/logcat-uwb-crash.txt`: exact crash excerpt supplied for this lab.

The Day 2 brief also asks for a screenshot of Logcat. The emulator's current log buffer no longer contains the supplied UWB event, so that screenshot has not been captured. The text excerpt is retained; capture the Logcat pane when the event is available to complete the screenshot requirement.
