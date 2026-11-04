# Day 2: Code Walkthrough

## Current screen and click flow

The current app has one Activity and one layout. It does not yet contain a separate Home screen, Task Details screen, or navigation function, so this walkthrough documents the flow that exists in the project.

1. Android launches `EmployeeTaskManagerActivity`, registered as the launcher Activity in `AndroidManifest.xml`.
2. `onCreate()` calls `setContentView(R.layout.employeetaskmanager)`, which displays `employeetaskmanager.xml`.
3. The layout defines the button with ID `@+id/button` and the label “click me”.
4. `findViewById<Button>(R.id.button)` gets that view in the Activity.
5. `setOnClickListener { ... }` handles a tap.
6. The listener displays the “Great u clicked me” Toast.

## Files and functions followed

- `app/src/main/AndroidManifest.xml`: declares the launcher Activity.
- `app/src/main/java/com/example/employee_task_manager/EmployeeTaskManagerActivity.kt`: `onCreate()`, button lookup, and click listener.
- `app/src/main/res/layout/employeetaskmanager.xml`: button and text layout.

## Debug verification

To trace the click:

1. Open `EmployeeTaskManagerActivity.kt` and set a breakpoint on the `Toast.makeText(...)` line.
2. Start the app with **Debug**.
3. Tap **click me** in the emulator.
4. Confirm execution pauses at the breakpoint, then resume and confirm the Toast appears.
5. Save a screenshot of the paused debugger state and one of the Logcat evidence under `docs/day2/screenshots/`.

The lab slide asks for a Home-to-Task-Details trace. That flow is not present in the current project, so it should be added to this walkthrough only after those screens/navigation exist.
