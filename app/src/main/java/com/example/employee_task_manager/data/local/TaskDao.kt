package com.example.employee_task_manager.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY updatedAt DESC")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("SELECT * FROM tasks WHERE syncState = 0")
    suspend fun getPendingTasks(): List<TaskEntity>

    @Query("UPDATE tasks SET syncState = :syncState WHERE id = :taskId")
    suspend fun updateSyncState(taskId: Int, syncState: Int)

    @Query("UPDATE tasks SET syncState = 0 WHERE syncState = 1")
    suspend fun resetSyncingTasksToPending()
}
