package com.example.employee_task_manager.data.repository

import com.example.employee_task_manager.data.Task
import com.example.employee_task_manager.data.local.TaskDao
import com.example.employee_task_manager.data.local.TaskEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class RoomTaskRepository(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val tasks: Flow<List<Task>> = taskDao.observeTasks().map { entities ->
        entities.map { entity ->
            Task(
                id = entity.id,
                title = entity.title,
                description = if (entity.description.isNotBlank()) "${entity.description} • Priority: ${entity.priority}" else "Priority: ${entity.priority} • ID: ${entity.id}",
                isCompleted = entity.completed
            )
        }
    }

    suspend fun createTask(title: String, description: String = "", priority: String = "Medium"): Result<Unit> = withContext(ioDispatcher) {
        try {
            val trimmedTitle = title.trim()
            if (trimmedTitle.isBlank()) {
                return@withContext Result.Error(IllegalArgumentException("Task title cannot be empty or blank."))
            }
            val entity = TaskEntity(
                clientId = UUID.randomUUID().toString(),
                title = trimmedTitle,
                description = description.trim(),
                priority = priority,
                completed = false,
                updatedAt = System.currentTimeMillis(),
                syncState = 0 // PENDING
            )
            taskDao.insertTask(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(e)
        }
    }

    suspend fun toggleTaskCompletion(taskId: Int): Result<Unit> = withContext(ioDispatcher) {
        try {
            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                val updated = task.copy(
                    completed = !task.completed,
                    updatedAt = System.currentTimeMillis()
                )
                taskDao.updateTask(updated)
                Result.Success(Unit)
            } else {
                Result.Error(IllegalArgumentException("Task not found."))
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(e)
        }
    }
}
