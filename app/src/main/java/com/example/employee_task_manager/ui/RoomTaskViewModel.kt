package com.example.employee_task_manager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.employee_task_manager.data.Task
import com.example.employee_task_manager.data.repository.Result
import com.example.employee_task_manager.data.repository.RoomTaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface RoomTaskUiState {
    object Loading : RoomTaskUiState
    data class Success(val tasks: List<Task>) : RoomTaskUiState
    object Empty : RoomTaskUiState
    data class Error(val message: String) : RoomTaskUiState
}

class RoomTaskViewModel(private val repository: RoomTaskRepository) : ViewModel() {

    private val processingTaskIds = mutableSetOf<Int>()

    val uiState: StateFlow<RoomTaskUiState> = repository.tasks
        .map<List<Task>, RoomTaskUiState> { tasks ->
            if (tasks.isEmpty()) {
                RoomTaskUiState.Empty
            } else {
                RoomTaskUiState.Success(tasks)
            }
        }
        .catch { e ->
            if (e is CancellationException) throw e
            emit(RoomTaskUiState.Error(e.localizedMessage ?: "Failed to load tasks from database."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RoomTaskUiState.Loading
        )

    fun createTask(title: String, description: String = "", priority: String = "Medium", onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val trimmed = title.trim()
            if (trimmed.isBlank()) {
                onError("Task title cannot be empty or blank.")
                return@launch
            }
            when (val result = repository.createTask(trimmed, description, priority)) {
                is Result.Success -> {}
                is Result.Error -> {
                    onError(result.exception.localizedMessage ?: "Failed to create task.")
                }
            }
        }
    }

    fun toggleTaskCompletion(taskId: Int, onError: (String) -> Unit = {}) {
        synchronized(processingTaskIds) {
            if (processingTaskIds.contains(taskId)) return
            processingTaskIds.add(taskId)
        }
        viewModelScope.launch {
            try {
                when (val result = repository.toggleTaskCompletion(taskId)) {
                    is Result.Success -> {}
                    is Result.Error -> {
                        onError(result.exception.localizedMessage ?: "Failed to update task status.")
                    }
                }
            } finally {
                synchronized(processingTaskIds) {
                    processingTaskIds.remove(taskId)
                }
            }
        }
    }
}
