package com.example.employee_task_manager.data

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false
)
