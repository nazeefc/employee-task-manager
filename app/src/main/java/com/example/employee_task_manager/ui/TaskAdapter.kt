package com.example.employee_task_manager.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.employee_task_manager.R
import com.example.employee_task_manager.data.Task

class TaskAdapter(
    private val tasks: MutableList<Task> = mutableListOf(),
    private val onStatusClick: ((Task) -> Unit)? = null,
    private val onTaskClick: ((Task) -> Unit)? = null
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textTaskTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textTaskDescription)
        val statusTextView: TextView = itemView.findViewById(R.id.textTaskStatus)
        val priorityTextView: TextView = itemView.findViewById(R.id.textTaskPriority)
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.title
        if (task.description.isBlank()) {
            holder.descriptionTextView.visibility = View.GONE
        } else {
            holder.descriptionTextView.visibility = View.VISIBLE
            holder.descriptionTextView.text = task.description
        }

        val context = holder.itemView.context
        val shape = GradientDrawable().apply {
            cornerRadius = 16f
        }

        // Priority badge styling
        if (task.description.contains("High")) {
            holder.priorityTextView.text = "HIGH"
            holder.priorityTextView.setTextColor(Color.parseColor("#C62828"))
            shape.setColor(Color.parseColor("#FFEBEE"))
        } else if (task.description.contains("Low")) {
            holder.priorityTextView.text = "LOW"
            holder.priorityTextView.setTextColor(Color.parseColor("#1565C0"))
            shape.setColor(Color.parseColor("#E3F2FD"))
        } else {
            holder.priorityTextView.text = "MEDIUM"
            holder.priorityTextView.setTextColor(Color.parseColor("#E65100"))
            shape.setColor(Color.parseColor("#FFF3E0"))
        }
        holder.priorityTextView.background = shape

        if (task.isCompleted) {
            holder.statusTextView.text = "✓ Completed"
            holder.statusTextView.setTextColor(context.getColor(android.R.color.holo_green_dark))
        } else {
            holder.statusTextView.text = "⏳ Pending"
            holder.statusTextView.setTextColor(context.getColor(android.R.color.holo_orange_dark))
        }

        holder.itemView.setOnClickListener {
            onTaskClick?.invoke(task)
        }

        holder.statusTextView.setOnClickListener {
            onStatusClick?.invoke(task)
        }
    }

    override fun getItemCount(): Int = tasks.size
}
