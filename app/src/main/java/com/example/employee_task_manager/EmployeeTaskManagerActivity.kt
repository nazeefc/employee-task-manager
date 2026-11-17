package com.example.employee_task_manager

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.example.employee_task_manager.data.Task
import com.example.employee_task_manager.data.local.AppDatabase
import com.example.employee_task_manager.data.repository.RoomTaskRepository
import com.example.employee_task_manager.ui.RoomTaskUiState
import com.example.employee_task_manager.ui.RoomTaskViewModel
import com.example.employee_task_manager.ui.TaskAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EmployeeTaskManagerActivity : AppCompatActivity() {

    private lateinit var roomViewModel: RoomTaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var statusTextView: TextView
    private lateinit var refreshButton: Button
    private lateinit var createTaskButton: Button

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
        createTaskButton = findViewById(R.id.buttonCreateTask)
        progressBar = findViewById(R.id.progressBarLoading)
        statusTextView = findViewById(R.id.textStatusMessage)

        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(
            onStatusClick = { task ->
                roomViewModel.toggleTaskCompletion(task.id) { errorMsg ->
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                }
            },
            onTaskClick = { task ->
                showTaskDetailsDialog(task)
            }
        )
        recyclerView.adapter = taskAdapter

        // Setup Room Database, Repository, and ViewModel
        val database = AppDatabase.getDatabase(this)
        val roomRepository = RoomTaskRepository(database.taskDao())

        roomViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RoomTaskViewModel(roomRepository) as T
            }
        })[RoomTaskViewModel::class.java]

        createTaskButton.setOnClickListener {
            showCreateTaskDialog(roomViewModel)
        }

        refreshButton.setOnClickListener {
            Toast.makeText(this, "Task list is up to date (Local Room Database)", Toast.LENGTH_SHORT).show()
        }

        // Collect UI state lifecycle-safely from Room Database Flow
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                roomViewModel.uiState.collectLatest { state ->
                    renderRoomState(state)
                }
            }
        }
    }

    private fun renderRoomState(state: RoomTaskUiState) {
        when (state) {
            is RoomTaskUiState.Loading -> {
                progressBar.visibility = View.VISIBLE
                statusTextView.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
            is RoomTaskUiState.Success -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                statusTextView.visibility = View.GONE
                taskAdapter.updateTasks(state.tasks)
            }
            is RoomTaskUiState.Empty -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                statusTextView.visibility = View.VISIBLE
                statusTextView.text = getString(R.string.no_tasks_found)
            }
            is RoomTaskUiState.Error -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.GONE
                statusTextView.visibility = View.VISIBLE
                statusTextView.text = state.message
            }
        }
    }

    private fun showCreateTaskDialog(viewModel: RoomTaskViewModel) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
        }

        val titleInput = EditText(this).apply {
            hint = "Task Title (Required)"
        }
        val descInput = EditText(this).apply {
            hint = "Description (Optional)"
        }

        val priorityLabel = TextView(this).apply {
            text = "Priority:"
            setPadding(0, 16, 0, 8)
        }

        val prioritySpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@EmployeeTaskManagerActivity,
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf("Low", "Medium", "High")
            )
            setSelection(1) // Default to Medium
        }

        container.addView(titleInput)
        container.addView(descInput)
        container.addView(priorityLabel)
        container.addView(prioritySpinner)

        AlertDialog.Builder(this)
            .setTitle("Create New Task")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val title = titleInput.text.toString()
                val description = descInput.text.toString()
                val priority = prioritySpinner.selectedItem.toString()

                if (title.isBlank()) {
                    Toast.makeText(this, "Task title cannot be blank.", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.createTask(title, description, priority) { errorMsg ->
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showTaskDetailsDialog(task: Task) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
        }

        val titleView = TextView(this).apply {
            text = task.title
            textSize = 20f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }

        val detailsView = TextView(this).apply {
            text = "${task.description}\n\nStatus: ${if (task.isCompleted) "Completed" else "Pending"}"
            textSize = 15f
            setPadding(0, 0, 0, 24)
        }

        container.addView(titleView)
        container.addView(detailsView)

        AlertDialog.Builder(this)
            .setTitle("Task Details")
            .setView(container)
            .setPositiveButton("Toggle Status") { _, _ ->
                roomViewModel.toggleTaskCompletion(task.id) { errorMsg ->
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Close", null)
            .show()
    }
}
