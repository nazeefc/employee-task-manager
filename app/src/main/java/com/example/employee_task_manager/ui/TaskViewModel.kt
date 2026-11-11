package com.example.employee_task_manager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employee_task_manager.data.Task
import com.example.employee_task_manager.data.repository.Result
import com.example.employee_task_manager.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Success(
        val tasks: List<Task>,
        val isRefreshing: Boolean = false,
        val errorMessage: String? = null
    ) : TaskUiState
    object Empty : TaskUiState
    data class Error(val message: String) : TaskUiState
}

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    private var cachedTasks: List<Task> = emptyList()
    private var fetchJob: Job? = null

    init {
        fetchTasks(isRefresh = false)
    }

    fun fetchTasks(isRefresh: Boolean = false) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            if (!isRefresh || cachedTasks.isEmpty()) {
                _uiState.value = TaskUiState.Loading
            } else {
                _uiState.value = TaskUiState.Success(cachedTasks, isRefreshing = true)
            }

            when (val result = repository.getTasks()) {
                is Result.Success -> {
                    cachedTasks = result.data
                    if (result.data.isEmpty()) {
                        _uiState.value = TaskUiState.Empty
                    } else {
                        _uiState.value = TaskUiState.Success(result.data)
                    }
                }
                is Result.Error -> {
                    val errorMessage = classifyError(result.exception)
                    if (cachedTasks.isNotEmpty()) {
                        _uiState.value = TaskUiState.Success(
                            tasks = cachedTasks,
                            isRefreshing = false,
                            errorMessage = errorMessage
                        )
                    } else {
                        _uiState.value = TaskUiState.Error(errorMessage)
                    }
                }
            }
        }
    }

    private fun classifyError(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> "Internet Error: Network connectivity unavailable or request timed out."
            is HttpException -> {
                val code = throwable.code()
                when {
                    code in 500..599 -> "Server Error ($code): Unsuccessful server response."
                    code in 400..499 -> "API/Request Error ($code): Client request failure."
                    else -> "API/Request Error ($code): HTTP error."
                }
            }
            else -> "Invalid Response Error: ${throwable.localizedMessage ?: "Failed to parse server response."}"
        }
    }
}
