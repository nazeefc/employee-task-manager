package com.example.employee_task_manager.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = database.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndObserveTask() = runBlocking {
        val task = TaskEntity(
            clientId = "client-uuid-1",
            title = "Test Task Room",
            completed = false,
            updatedAt = System.currentTimeMillis(),
            syncState = 0
        )
        taskDao.insertTask(task)

        val tasks = taskDao.observeTasks().first()
        assertEquals(1, tasks.size)
        assertEquals("Test Task Room", tasks[0].title)
        assertEquals("client-uuid-1", tasks[0].clientId)
        assertEquals(false, tasks[0].completed)
    }

    @Test
    fun updateTask() = runBlocking {
        val task = TaskEntity(
            clientId = "client-uuid-2",
            title = "Initial Title",
            completed = false
        )
        taskDao.insertTask(task)

        val inserted = taskDao.observeTasks().first().first()
        val updated = inserted.copy(title = "Updated Title", completed = true)
        taskDao.updateTask(updated)

        val tasks = taskDao.observeTasks().first()
        assertEquals(1, tasks.size)
        assertEquals("Updated Title", tasks[0].title)
        assertTrue(tasks[0].completed)
    }

    @Test
    fun deleteTask() = runBlocking {
        val task = TaskEntity(
            clientId = "client-uuid-3",
            title = "Task To Delete"
        )
        taskDao.insertTask(task)

        val inserted = taskDao.observeTasks().first().first()
        taskDao.deleteTask(inserted.id)

        val tasks = taskDao.observeTasks().first()
        assertTrue(tasks.isEmpty())
    }

    @Test(expected = android.database.sqlite.SQLiteConstraintException::class)
    fun rejectDuplicateClientId() = runBlocking {
        val task1 = TaskEntity(
            clientId = "duplicate-uuid",
            title = "Task 1"
        )
        val task2 = TaskEntity(
            clientId = "duplicate-uuid",
            title = "Task 2"
        )
        taskDao.insertTask(task1)
        taskDao.insertTask(task2)
    }
}
