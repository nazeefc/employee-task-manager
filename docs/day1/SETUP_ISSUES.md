# Day 1 — Setup issues log

Documented during Android Developer Bootcamp Week 1 (Tuesday, Nov 3, 2026).

## Issue 1 — `java` not on system PATH

- **Symptom:** `java -version` fails in PowerShell (`CommandNotFoundException`).
- **Impact:** Terminal JDK checks fail; Android Studio still builds using its bundled JDK / Gradle toolchain.
- **Workaround:** Use Android Studio build / `gradlew` (toolchain resolver is enabled in `settings.gradle.kts`). Optionally add Android Studio’s JBR to PATH, e.g. `C:\Program Files\Android\Android Studio\jbr\bin`.
- **Status:** Non-blocking for APK build. Open for PATH cleanup.

## Issue 2 — Codex CLI not installed / not on PATH

- **Symptom:** `codex --version` fails (`CommandNotFoundException`).
- **Impact:** Day 1 checklist item for Codex CLI is incomplete from the terminal.
- **Workaround for Day 1:** Cursor agent was used to identify the main Activity and explain the project. Install Codex CLI later for Days 3–4 reviews.
- **Status:** Open — install Node-based Codex CLI when available for the course.

## Issue 3 — No device attached at first build

- **Symptom:** `adb devices` showed an empty list after `assembleDebug` succeeded.
- **Impact:** Could not launch until an emulator was started.
- **Fix:** Started AVD `nazeef_app`, waited for `sys.boot_completed=1`, then installed/launched the app.
- **Status:** Resolved.

## Issue 4 — Java → Kotlin source migration already in progress

- **Symptom:** Git status showed deleted `.java` sources and new `.kt` equivalents for Activity + tests.
- **Impact:** None for Day 1 exit gate; starter remains View-based AppCompat.
- **Status:** Included in Day 1 commit so the repo matches the working IDE project.
