package com.example.employee_task_manager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.employee_task_manager.R
import com.example.employee_task_manager.data.Task

class TaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textTaskTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textTaskDescription)
        val statusTextView: TextView = itemView.findViewById(R.id.textTaskStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.title
        holder.descriptionTextView.text = task.description
        if (task.isCompleted) {
            holder.statusTextView.text = "Completed"
            holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        } else {
            holder.statusTextView.text = "Pending"
            holder.statusTextView.setTextColor(holder.itemView.context.getColor(android.R.color.holo_orange_dark))
        }
    }

    override fun getItemCount(): Int = tasks.size
}
