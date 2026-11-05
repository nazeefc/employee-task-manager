package com.example.employee_task_manager

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EmployeeTaskManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.employeetaskmanager)


        val clickButton = findViewById<Button>(R.id.button)
        clickButton.setOnClickListener {
            Toast.makeText(this, "Great u clicked me", Toast.LENGTH_SHORT).show()
        }

    }


}
