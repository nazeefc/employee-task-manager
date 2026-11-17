package com.example.employee_task_manager.ui

import app.cash.turbine.test
import com.example.employee_task_manager.data.local.TaskDao
import com.example.employee_task_manager.data.local.TaskEntity
import com.example.employee_task_manager.data.repository.Result
import com.example.employee_task_manager.data.repository.RoomTaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoomTaskViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeDao: FakeTaskDao
    private lateinit var repository: RoomTaskRepository
    private lateinit var viewModel: RoomTaskViewModel

    @Before
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeTaskDao()
        repository = RoomTaskRepository(fakeDao, testDispatcher)
        viewModel = RoomTaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty database emits Loading then Empty state`() = runTest {
        viewModel.uiState.test {
            assertEquals(RoomTaskUiState.Loading, awaitItem())
            assertEquals(RoomTaskUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `non-empty database emits Success state with tasks`() = runTest {
        fakeDao.tasksFlow.value = listOf(
            TaskEntity(id = 1, clientId = "uuid-1", title = "Room Task 1", completed = false)
        )

        viewModel.uiState.test {
            assertEquals(RoomTaskUiState.Loading, awaitItem())
            val success = awaitItem() as RoomTaskUiState.Success
            assertEquals(1, success.tasks.size)
            assertEquals("Room Task 1", success.tasks[0].title)
        }
    }

    @Test
    fun `reactive insert emits updated task list in Success state`() = runTest {
        viewModel.uiState.test {
            assertEquals(RoomTaskUiState.Loading, awaitItem())
            assertEquals(RoomTaskUiState.Empty, awaitItem())

            fakeDao.insertTask(TaskEntity(id = 1, clientId = "uuid-dyn", title = "Dynamic Task", completed = false))

            val success = awaitItem() as RoomTaskUiState.Success
            assertEquals(1, success.tasks.size)
            assertEquals("Dynamic Task", success.tasks[0].title)
        }
    }

    @Test
    fun `dao observation flow error emits Error state`() = runTest {
        val errorDao = object : TaskDao by FakeTaskDao() {
            override fun observeTasks(): Flow<List<TaskEntity>> = flow {
                throw RuntimeException("Database observation failed")
            }
        }
        val errorRepo = RoomTaskRepository(errorDao, testDispatcher)
        val errorViewModel = RoomTaskViewModel(errorRepo)

        errorViewModel.uiState.test {
            assertEquals(RoomTaskUiState.Loading, awaitItem())
            val errState = awaitItem() as RoomTaskUiState.Error
            assertTrue(errState.message.contains("Database observation failed"))
        }
    }

    @Test(expected = CancellationException::class)
    fun `createTask rethrows CancellationException`() = runTest {
        val cancellingDao = object : TaskDao by FakeTaskDao() {
            override suspend fun insertTask(task: TaskEntity) {
                throw CancellationException("Operation cancelled")
            }
        }
        val cancellingRepo = RoomTaskRepository(cancellingDao, testDispatcher)
        cancellingRepo.createTask("Cancelled Task")
    }

    @Test
    fun `createTask with valid title inserts task successfully`() = runTest {
        var errorObserved: String? = null
        viewModel.createTask("New Local Task", "Description", "High") { err ->
            errorObserved = err
        }

        advanceUntilIdle()
        assertEquals(null, errorObserved)
        assertEquals(1, fakeDao.insertedTasks.size)
        assertEquals("New Local Task", fakeDao.insertedTasks[0].title)
        assertEquals("Description", fakeDao.insertedTasks[0].description)
        assertEquals("High", fakeDao.insertedTasks[0].priority)
    }

    @Test
    fun `createTask with blank title invokes error callback and rejects insertion`() = runTest {
        var errorObserved: String? = null
        viewModel.createTask("   ") { err ->
            errorObserved = err
        }

        advanceUntilIdle()
        assertEquals("Task title cannot be empty or blank.", errorObserved)
        assertTrue(fakeDao.insertedTasks.isEmpty())
    }

    @Test
    fun `repository handles database error gracefully`() = runTest {
        fakeDao.shouldThrow = true
        val result = repository.createTask("Crash Task")
        assertTrue(result is Result.Error)
    }
}

class FakeTaskDao : TaskDao {
    val tasksFlow = MutableStateFlow<List<TaskEntity>>(emptyList())
    val insertedTasks = mutableListOf<TaskEntity>()
    var shouldThrow = false

    override suspend fun insertTask(task: TaskEntity) {
        if (shouldThrow) throw RuntimeException("DB Error")
        insertedTasks.add(task)
        tasksFlow.update { list -> list + task }
    }

    override suspend fun updateTask(task: TaskEntity) {
        if (shouldThrow) throw RuntimeException("DB Error")
    }

    override fun observeTasks(): Flow<List<TaskEntity>> = tasksFlow

    override suspend fun getTaskById(taskId: Int): TaskEntity? = insertedTasks.firstOrNull { it.id == taskId }

    override suspend fun deleteTask(taskId: Int) {}
}
