package com.example.employee_task_manager.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.employee_task_manager.data.Task
import com.example.employee_task_manager.data.local.TaskDao
import com.example.employee_task_manager.data.local.TaskEntity
import com.example.employee_task_manager.data.worker.TaskSyncWorker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.TimeUnit

class RoomTaskRepository(
    private val taskDao: TaskDao,
    private val context: Context? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    constructor(taskDao: TaskDao, ioDispatcher: CoroutineDispatcher) : this(taskDao, null, ioDispatcher)
    constructor(taskDao: TaskDao, context: Context?) : this(taskDao, context, Dispatchers.IO)

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

            // Schedule WorkManager background synchronization with REPLACE policy to prevent queue race
            context?.let { ctx ->
                try {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    val syncWorkRequest = OneTimeWorkRequestBuilder<TaskSyncWorker>()
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                            BackoffPolicy.EXPONENTIAL,
                            WorkRequest.MIN_BACKOFF_MILLIS,
                            TimeUnit.MILLISECONDS
                        )
                        .build()

                    WorkManager.getInstance(ctx.applicationContext).enqueueUniqueWork(
                        "TaskSyncWork",
                        ExistingWorkPolicy.REPLACE,
                        syncWorkRequest
                    )
                } catch (e: Exception) {
                    // Suppress if WorkManager initialization fails in isolated test contexts
                }
            }

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
