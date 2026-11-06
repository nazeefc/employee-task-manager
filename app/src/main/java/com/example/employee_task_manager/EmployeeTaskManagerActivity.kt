package com.example.employee_task_manager

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.employee_task_manager.data.Task
import com.example.employee_task_manager.ui.TaskAdapter

class EmployeeTaskManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employeetaskmanager)

        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.rootLayout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val refreshButton = findViewById<Button>(R.id.buttonRefreshTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample task list for Task List UI feature implementation
        val sampleTasks = listOf(
            Task(1, "Review Daily Reports", "Review morning metrics and submit daily summary to manager.", false),
            Task(2, "Update Project Documentation", "Document architecture and development plan for Task List feature.", true),
            Task(3, "Team Sync Meeting", "Discuss weekly goals and sprint progress with the team.", false),
            Task(4, "Code Refactoring", "Clean up utility classes and improve error handling.", false)
        )

        recyclerView.adapter = TaskAdapter(sampleTasks)

        refreshButton.setOnClickListener {
            recyclerView.adapter = TaskAdapter(sampleTasks)
        }
    }
}
