# AGENTS.md - Guidelines for AI Agents

Welcome to the **Employee Task Manager** (`employeetaskmanager`) Android project workspace.

## Project Overview
- **Name:** Employee Task Manager (`employeetaskmanager`)
- **Current State:** Day 3 REST API Integration phase. Read-only task dashboard (`EmployeeTaskManagerActivity.kt`) fetching tasks via Retrofit from `GET https://jsonplaceholder.typicode.com/todos` using MVVM + Repository architecture and `StateFlow`.
- **Broader Business Goal:** Employees should be able to view tasks, add tasks, mark tasks complete, and see their status.
- **Approved Day 3 Implementation Scope:** The current implementation is limited to fetching tasks through the JSONPlaceholder GET API, displaying task information and completion status, supporting refresh, and showing loading, empty, and error states. Adding tasks and editing completion status are not implemented in Day 3.

## Guidelines for AI Agents
1. **Plan Before Coding:** Always follow the planning workflow and get trainer/user approval before modifying any application code.
2. **Strict File Permissions:**
   - **Allowed:** Read/inspect code, create/update documentation files (`docs/`, `AGENTS.md`, etc.), create architecture diagrams.
   - **Not Allowed:** Modify Kotlin/Java source code, modify XML layouts, modify Gradle files, implement application features, delete/rename existing files, install dependencies without approval.
3. **Architecture:** Follow MVVM (Model-View-ViewModel), Repository pattern, and clean architecture principles.
4. **Documentation:** Keep documentation up-to-date in `docs/` and record all decisions, debugging steps, and current status.
