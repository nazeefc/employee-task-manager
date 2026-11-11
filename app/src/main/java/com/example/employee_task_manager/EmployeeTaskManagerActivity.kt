package com.example.employee_task_manager

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.employee_task_manager.data.remote.TodoApiService
import com.example.employee_task_manager.data.repository.TaskRepository
import com.example.employee_task_manager.ui.TaskAdapter
import com.example.employee_task_manager.ui.TaskUiState
import com.example.employee_task_manager.ui.TaskViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EmployeeTaskManagerActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var statusTextView: TextView
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employeetaskmanager)

        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.rootLayout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewTasks)
        refreshButton = findViewById(R.id.buttonRefreshTasks)
        progressBar = findViewById(R.id.progressBarLoading)
        statusTextView = findViewById(R.id.textStatusMessage)

        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter()
        recyclerView.adapter = taskAdapter

        // Setup Retrofit, Repository, and ViewModel
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(TodoApiService::class.java)
        val repository = TaskRepository(apiService)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TaskViewModel(repository) as T
            }
        })[TaskViewModel::class.java]

        refreshButton.setOnClickListener {
            viewModel.fetchTasks(isRefresh = true)
        }

        // Collect UI state lifecycle-safely
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
                statusTextView.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
            is TaskUiState.Success -> {
                progressBar.visibility = if (state.isRefreshing) View.VISIBLE else View.GONE
                recyclerView.visibility = View.VISIBLE
                taskAdapter.updateTasks(state.tasks)

                if (state.errorMessage != null) {
                    statusTextView.visibility = View.VISIBLE
                    statusTextView.text = state.errorMessage
                } else {
                    statusTextView.visibility = View.GONE
                }
            }
            is TaskUiState.Empty -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                statusTextView.visibility = View.VISIBLE
                statusTextView.text = getString(R.string.no_tasks_found)
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
