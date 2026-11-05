# Employee Task Manager (`employeetaskmanager`)

Employee Task Manager is an Android application developed as part of the Android Developer Bootcamp. The app enables employees to view tasks, add tasks, mark tasks complete, and see their status.

## Requirements

- Android Studio with its bundled JDK
- Android SDK Platform 35 and Android SDK Platform Tools
- An Android emulator or a physical Android device with USB debugging enabled

The app supports Android 7.0 (API 24) and newer. It uses Java 11 compatibility and includes the Gradle wrapper, so a separate JDK or Gradle installation is not needed. Gradle sync requires an internet connection to download the Android Gradle Plugin and dependencies.

## Documentation & Setup Notes

- Day 1 environment setup and troubleshooting details are documented in `docs/day1/SETUP_ISSUES.md` and screenshots in `docs/day1/screenshots/`.

## Get the project

Open the existing project folder directly in Android Studio via **File → Open**.

## Sync and run

1. In Android Studio, choose **File → Open** and select the project root.
2. Allow Gradle sync to complete. If prompted, install Android SDK Platform 35.
3. Start an emulator from Device Manager, or connect a phone with USB debugging enabled.
4. Select the `app` run configuration and click **Run**.

To build and install from PowerShell at the project root:

```powershell
.\gradlew.bat :app:installDebug
```

To launch the installed app from the terminal:

```powershell
adb shell am start -n com.example.employee_task_manager/.EmployeeTaskManagerActivity
```

If `adb devices` shows no device, start the emulator or check the phone's USB debugging authorization.
