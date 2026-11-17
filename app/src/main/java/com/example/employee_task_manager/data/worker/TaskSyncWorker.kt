package com.example.employee_task_manager.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.employee_task_manager.BuildConfig
import com.example.employee_task_manager.data.local.AppDatabase
import com.example.employee_task_manager.data.remote.TaskSyncApiService
import com.example.employee_task_manager.data.remote.TaskUploadRequest
import kotlinx.coroutines.CancellationException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TaskSyncWorker(
    appContext: Context,
    params: WorkerParameters,
    private val apiService: TaskSyncApiService? = null,
    private val database: AppDatabase? = null
) : CoroutineWorker(appContext, params) {

    constructor(context: Context, params: WorkerParameters) : this(context, params, null, null)

    override suspend fun doWork(): Result {
        val db = database ?: AppDatabase.getDatabase(applicationContext)
        val taskDao = db.taskDao()

        try {
            // Recover interrupted work before checking whether this build has a backend.
            taskDao.resetSyncingTasksToPending()

            if (apiService == null && BuildConfig.SYNC_BASE_URL.isBlank()) {
                // No backend is configured. Leave tasks pending for a future configured build.
                return Result.failure()
            }

            val api = apiService ?: Retrofit.Builder()
                .baseUrl(BuildConfig.SYNC_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TaskSyncApiService::class.java)

            val pendingTasks = taskDao.getPendingTasks()
            if (pendingTasks.isEmpty()) {
                return Result.success()
            }

            var allSuccess = true
            var hasRetryableFailure = false

            for (task in pendingTasks) {
                // Transition: PENDING -> SYNCING (1)
                taskDao.updateSyncState(task.id, 1)

                val request = TaskUploadRequest(
                    clientId = task.clientId,
                    title = task.title,
                    description = task.description,
                    priority = task.priority,
                    completed = task.completed
                )

                try {
                    val response = api.uploadTask(request)
                    if (response.isSuccessful) {
                        // Transition: SYNCING -> SYNCED (2)
                        taskDao.updateSyncState(task.id, 2)
                    } else {
                        val code = response.code()
                        if (code in 500..599) {
                            taskDao.updateSyncState(task.id, 0)
                            hasRetryableFailure = true
                            allSuccess = false
                        } else if (code in 400..499) {
                            // Permanent 4xx failure: mark as permanent error (3) without infinite retry loops
                            taskDao.updateSyncState(task.id, 3)
                            allSuccess = false
                        } else {
                            taskDao.updateSyncState(task.id, 0)
                            hasRetryableFailure = true
                            allSuccess = false
                        }
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    taskDao.updateSyncState(task.id, 0)
                    hasRetryableFailure = true
                    allSuccess = false
                }
            }

            return if (allSuccess) Result.success() else if (hasRetryableFailure) Result.retry() else Result.failure()

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return Result.retry()
        }
    }
}
