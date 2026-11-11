# Employee Task Manager (`employeetaskmanager`)

Employee Task Manager is an Android application developed as part of the Android Developer Bootcamp. The current Day 3 implementation provides a **read-only task dashboard** that fetches tasks via the public JSONPlaceholder GET API (`GET https://jsonplaceholder.typicode.com/todos`).

## Features
- **Task List Dashboard:** Displays tasks retrieved from the remote REST API in a RecyclerView.
- **Task Information:** Shows task title, generated description (displaying Task ID and User ID), and completion status (Pending / Completed).
- **Refresh Functionality:** A Refresh button fetches the latest task data from the API with temporary in-memory state retention and error handling.
- **UI States:** Handles Loading, Success, Empty, Internet Error, Server Error, API/Request Error, and Invalid Response Error states.

Note: Task creation (adding tasks) and completion editing (marking tasks complete/incomplete) are not implemented in the current read-only GET scope.

## Requirements

- Android Studio with its bundled JDK
- Android SDK Platform 35 and Android SDK Platform Tools
- An Android emulator or a physical Android device with USB debugging enabled

The app supports Android 7.0 (API 24) and newer. It uses Java 11 compatibility and includes the Gradle wrapper. Setup notes and historical Day 1 documentation are available in `docs/day1/SETUP_ISSUES.md`.

## Sync and run

1. In Android Studio, choose **File → Open** and select the project root.
2. Allow Gradle sync to complete.
3. Start an emulator from Device Manager, or connect a phone with USB debugging enabled.
4. Select the `app` run configuration and click **Run**.

To build and install from PowerShell at the project root:

```powershell
.\gradlew.bat :app:installDebug
```
