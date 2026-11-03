# Employee Task Manager

A Kotlin Android starter app created for Android Developer Bootcamp Week 1 (Nov 3–6, 2026). The Gradle root project is named `AndroidTraining`.

## Project setup

### Prerequisites

- Android Studio stable with its bundled JDK (the project uses Java 11 compatibility).
- Android SDK Platform 35 and Android SDK Platform Tools (`adb`).
- An Android emulator or a physical device with USB debugging enabled.
- Git.
- Node.js and npm are optional for the Android build; they were checked as part of today's environment setup.

Tools recorded on Nov 3, 2026:

| Tool | Recorded result |
|---|---|
| Android SDK / ADB | ADB 1.0.41 (37.0.1-15733141) |
| Emulator | AVD `nazeef_app`, device `emulator-5554` |
| Git | 2.55.0.windows.5 |
| Node.js | v22.23.2 |
| npm | 10.9.8 |
| Java on system PATH | Not available; Android Studio's bundled JDK works for Gradle |
| Codex CLI | Not installed / not on PATH; not required to build |

The app uses Android Gradle Plugin 9.4.1, compile/target SDK 35, minimum SDK 24, and Java 11 source/target compatibility. Library and plugin versions are in `gradle/libs.versions.toml`.

### Clone and open

```bash
git clone https://github.com/nazeefc/employee-task-manager.git
cd employee-task-manager
```

In Android Studio, choose **File → Open**, select the cloned project root, and wait for Gradle sync to finish. The project includes the Gradle wrapper, so a separately installed Gradle is not needed.

### Start a device

Start an existing emulator from Android Studio's Device Manager, or use PowerShell:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -list-avds
& "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -avd nazeef_app
adb devices
```

For a physical device, enable Developer options and USB debugging, connect it, then confirm it appears in `adb devices`.

### Build, install, and launch

From the project root in PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
adb shell am start -n com.example.employee_task_manager/.EmployeeTaskManagerActivity
```

Alternatively, select the `app` run configuration in Android Studio and click **Run**. The application ID is `com.example.employee_task_manager`; its launcher activity is `EmployeeTaskManagerActivity`.

### Environment checks recorded today

Run these in PowerShell to inspect the local tools:

```powershell
java -version
git --version
node -v
npm -v
adb version
codex --version
```

On this setup, `java -version` and `codex --version` failed because those commands were not on PATH / installed. This did not block building: Android Studio/Gradle used the bundled JDK. The emulator was started after the first `adb devices` check returned an empty device list.

## Today's setup results

- `:app:assembleDebug` completed successfully.
- The app was installed and launched on AVD `nazeef_app` (`emulator-5554`).
- The launcher activity was confirmed from the manifest and app launch.
- A screenshot of the running app is saved at `docs/day1/screenshots/01-app-launched.png`.
- Setup issues and command output are documented in `docs/day1/SETUP_ISSUES.md` and `docs/day1/TOOL_CHECKS.md`.
- Day 1 checklist evidence is in `docs/day1/EXIT_GATE.md`.

## Project structure

```text
.
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── java/com/example/employee_task_manager/EmployeeTaskManagerActivity.kt
│       │   ├── res/
│       │   └── AndroidManifest.xml
│       ├── test/
│       └── androidTest/
├── docs/day1/
│   ├── EXIT_GATE.md
│   ├── SETUP_ISSUES.md
│   ├── TOOL_CHECKS.md
│   └── screenshots/
├── gradle/libs.versions.toml
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

## Git

The setup notes record the repository remote as `https://github.com/nazeefc/employee-task-manager.git`. To inspect or publish your local changes:

```bash
git status
git add .
git commit -m "Describe the change"
git push
```

## License / course note

Bootcamp coursework. The Employee Task Manager app will continue to grow through Week 1 (Days 2–4).
