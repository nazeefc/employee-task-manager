# Day 2: Code Walkthrough (Historical Snapshot)

> [!NOTE]
> **HISTORICAL RECORD:** This walkthrough documents the single-Activity scaffold and test button state from Day 2 of the project lifecycle. It does not represent the current Day 3 REST API implementation.

## Historical screen and click flow (Day 2)

The Day 2 app featured a single Activity and basic test button scaffold:

1. Android launches `EmployeeTaskManagerActivity`, registered as the launcher Activity in `AndroidManifest.xml`.
2. `onCreate()` calls `setContentView(R.layout.employeetaskmanager)`, which displays `employeetaskmanager.xml`.
3. The layout defined the test button with ID `@+id/button` and the label “click me”.
4. `findViewById<Button>(R.id.button)` retrieved that view in the Activity.
5. `setOnClickListener { ... }` handled a tap.
6. The listener displayed the “Great u clicked me” Toast.

## Files and functions followed (Day 2)

- `app/src/main/AndroidManifest.xml`: declares the launcher Activity.
- `app/src/main/java/com/example/employee_task_manager/EmployeeTaskManagerActivity.kt`: `onCreate()`, button lookup, and click listener.
- `app/src/main/res/layout/employeetaskmanager.xml`: button and text layout.

## Debug verification (Day 2)

To trace the historical click:

1. Open `EmployeeTaskManagerActivity.kt` and set a breakpoint on the `Toast.makeText(...)` line.
2. Start the app with **Debug**.
3. Tap **click me** in the emulator.
4. Confirm execution pauses at the breakpoint, then resume and confirm the Toast appears.
5. Save a screenshot of the paused debugger state and one of the Logcat evidence under `docs/day2/screenshots/`.
