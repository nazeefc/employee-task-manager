# Employee Task Manager

A Kotlin Android starter app. The Gradle root project is named `AndroidTraining`.

## Requirements

- Android Studio with its bundled JDK
- Android SDK Platform 35 and Platform Tools
- Android emulator or USB-debugging-enabled Android device

The app supports Android 7.0 (API 24) and newer. It uses Java 11 compatibility. Gradle is included via the project wrapper.

## Open and run

1. Open the project root in Android Studio and allow Gradle sync to finish.
2. Start an emulator or connect a device.
3. Select the `app` run configuration and click **Run**.

Or build and install from PowerShell at the project root:

```powershell
.\gradlew.bat :app:installDebug
```

The app ID is `com.example.employee_task_manager`; its launcher activity is `EmployeeTaskManagerActivity`.

## Setup notes

Android Studio's bundled JDK was used because Java was not on the system PATH. Node.js, npm, and Codex CLI are not required to build the app.

Day 1 setup details and evidence are in `docs/day1/`.
