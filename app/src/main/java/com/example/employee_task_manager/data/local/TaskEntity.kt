package com.example.employee_task_manager.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["clientId"], unique = true)]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientId: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val priority: String = "Medium", // "Low", "Medium", "High"
    val completed: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val syncState: Int = 0 // 0 = PENDING, 1 = SYNCING, 2 = SYNCED
)
