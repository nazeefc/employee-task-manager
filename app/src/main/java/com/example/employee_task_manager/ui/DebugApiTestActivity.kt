package com.example.employee_task_manager.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.employee_task_manager.R
import com.example.employee_task_manager.data.remote.TodoApiService
import com.example.employee_task_manager.data.remote.TodoDto
import com.example.employee_task_manager.data.repository.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DelayedTodoApiService(private val wrapped: TodoApiService, private val delayMillis: Long = 7000) : TodoApiService {
    override suspend fun getTodos(): List<TodoDto> {
        val tasks = wrapped.getTodos()
        delay(delayMillis)
        return tasks
    }
}

class DebugApiTestActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var statusTextView: TextView
    private lateinit var refreshButton: Button
    private lateinit var switchNavButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employeetaskmanager)

        findViewById<TextView>(R.id.textHeaderTitle).text = "API Tasks (7s Delay)"
        findViewById<Button>(R.id.buttonCreateTask).visibility = View.GONE

        refreshButton = findViewById(R.id.buttonRefreshTasks)
        refreshButton.text = "Restart GET"

        switchNavButton = findViewById(R.id.buttonSwitchNav)
        switchNavButton.text = "My Tasks"

        progressBar = findViewById(R.id.progressBarLoading)
        statusTextView = findViewById(R.id.textStatusMessage)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter()
        recyclerView.adapter = taskAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val rawApiService = retrofit.create(TodoApiService::class.java)
        val delayedApiService = DelayedTodoApiService(rawApiService, 7000)
        val repository = TaskRepository(delayedApiService)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TaskViewModel(repository) as T
            }
        })[TaskViewModel::class.java]

        refreshButton.setOnClickListener {
            viewModel.fetchTasks(isRefresh = true)
        }

        switchNavButton.setOnClickListener {
            finish() // Return to My Tasks
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: TaskUiState) {
        when (state) {
            is TaskUiState.Loading -> {
                progressBar.visibility = View.VISIBLE
                statusTextView.visibility = View.VISIBLE
                statusTextView.text = "Loading 200 tasks (7s simulated delay)..."
                recyclerView.visibility = View.GONE
            }
            is TaskUiState.Success -> {
                progressBar.visibility = if (state.isRefreshing) View.VISIBLE else View.GONE
                recyclerView.visibility = View.VISIBLE
                statusTextView.visibility = View.VISIBLE
                if (state.isRefreshing) {
                    statusTextView.text = "Refreshing API tasks (7s simulated delay)..."
                } else if (state.errorMessage != null) {
                    statusTextView.text = state.errorMessage
                } else {
                    statusTextView.text = "Loaded ${state.tasks.size} API tasks successfully!"
                }
                taskAdapter.updateTasks(state.tasks)
            }
            is TaskUiState.Empty -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                statusTextView.visibility = View.VISIBLE
                statusTextView.text = "No tasks found"
            }
            is TaskUiState.Error -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                statusTextView.visibility = View.VISIBLE
                statusTextView.text = state.message
            }
        }
    }
}
