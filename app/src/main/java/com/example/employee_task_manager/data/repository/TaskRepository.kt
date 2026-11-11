package com.example.employee_task_manager.data.repository

import com.example.employee_task_manager.data.Task
import com.example.employee_task_manager.data.remote.TodoApiService
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable) : Result<Nothing>
}

class TaskRepository(private val apiService: TodoApiService) {
    suspend fun getTasks(): Result<List<Task>> {
        return try {
            val dtos = apiService.getTodos()
            val tasks = dtos.map { dto ->
                Task(
                    id = dto.id,
                    title = dto.title,
                    description = "Task ID: ${dto.id} • User ID: ${dto.userId}",
                    isCompleted = dto.completed
                )
            }
            Result.Success(tasks)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: HttpException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
