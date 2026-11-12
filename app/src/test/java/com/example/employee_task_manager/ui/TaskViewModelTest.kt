package com.example.employee_task_manager.ui

import app.cash.turbine.test
import com.example.employee_task_manager.data.remote.TodoApiService
import com.example.employee_task_manager.data.remote.TodoDto
import com.example.employee_task_manager.data.repository.Result
import com.example.employee_task_manager.data.repository.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeApiService = FakeTodoApiService()
    private val repository = TaskRepository(fakeApiService)
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful non-empty response emits Success state and maps DTO correctly`() = runTest {
        fakeApiService.todos = listOf(TodoDto(userId = 99, id = 42, title = "Exact Mapping Test", completed = true))
        viewModel = TaskViewModel(repository)

        viewModel.uiState.test {
            assertEquals(TaskUiState.Loading, awaitItem())
            val success = awaitItem() as TaskUiState.Success
            assertEquals(1, success.tasks.size)
            val task = success.tasks[0]
            assertEquals(42, task.id)
            assertEquals("Exact Mapping Test", task.title)
            assertEquals("Task ID: 42 • User ID: 99", task.description)
            assertEquals(true, task.isCompleted)
        }
    }

    @Test
    fun `successful empty response emits Empty state`() = runTest {
        fakeApiService.todos = emptyList()
        viewModel = TaskViewModel(repository)

        viewModel.uiState.test {
            assertEquals(TaskUiState.Loading, awaitItem())
            assertEquals(TaskUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `internet failure emits Error state when cache is empty`() = runTest {
        fakeApiService.exception = IOException("Network down")
        viewModel = TaskViewModel(repository)

        viewModel.uiState.test {
            assertEquals(TaskUiState.Loading, awaitItem())
            val error = awaitItem() as TaskUiState.Error
            assertTrue(error.message.contains("Internet Error"))
        }
    }

    @Test
    fun `malformed json parsing failure emits Invalid Response Error`() = runTest {
        fakeApiService.exception = IllegalArgumentException("Malformed JSON")
        viewModel = TaskViewModel(repository)

        viewModel.uiState.test {
            assertEquals(TaskUiState.Loading, awaitItem())
            val error = awaitItem() as TaskUiState.Error
            assertTrue(error.message.contains("Invalid Response Error"))
        }
    }

    @Test
    fun `actual retrofit gson converter malformed json response produces Invalid Response Error`() = runTest {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody("{ \"invalid\": json }").setResponseCode(200))
        server.start()

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(TodoApiService::class.java)
            val repo = TaskRepository(apiService)
            val vm = TaskViewModel(repo)

            vm.uiState.test {
                assertEquals(TaskUiState.Loading, awaitItem())
                testScheduler.runCurrent()
                val error = awaitItem() as TaskUiState.Error
                assertTrue(error.message.contains("Invalid Response Error"))
            }

            val request = server.takeRequest(3, java.util.concurrent.TimeUnit.SECONDS)
            assertEquals("/todos", request?.path)
            assertEquals("GET", request?.method)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `missing required title field in json produces Invalid Response Error`() = runTest {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody("[{\"userId\": 1, \"id\": 1, \"completed\": false}]").setResponseCode(200))
        server.start()

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(TodoApiService::class.java)
            val repo = TaskRepository(apiService)
            val vm = TaskViewModel(repo)

            vm.uiState.test {
                assertEquals(TaskUiState.Loading, awaitItem())
                testScheduler.runCurrent()
                val error = awaitItem() as TaskUiState.Error
                assertTrue(error.message.contains("Invalid Response Error"))
            }
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `explicit null value for non-nullable title field produces Invalid Response Error`() = runTest {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody("[{\"userId\": 1, \"id\": 1, \"title\": null, \"completed\": false}]").setResponseCode(200))
        server.start()

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(TodoApiService::class.java)
            val repo = TaskRepository(apiService)
            val vm = TaskViewModel(repo)

            vm.uiState.test {
                assertEquals(TaskUiState.Loading, awaitItem())
                testScheduler.runCurrent()
                val error = awaitItem() as TaskUiState.Error
                assertTrue(error.message.contains("Invalid Response Error"))
            }
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `blank or whitespace-only title in json produces Invalid Response Error with title validation message`() = runTest {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody("[{\"userId\": 1, \"id\": 1, \"title\": \"   \", \"completed\": false}]").setResponseCode(200))
        server.start()

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(TodoApiService::class.java)
            val repo = TaskRepository(apiService)
            val vm = TaskViewModel(repo)

            vm.uiState.test {
                assertEquals(TaskUiState.Loading, awaitItem())
                testScheduler.runCurrent()
                val error = awaitItem() as TaskUiState.Error
                assertTrue(error.message.contains("Invalid Response Error: Task title is required but missing or empty."))
            }
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `actual retrofit HTTP 500 server error integration test emits Server Error state and reaches server`() = runTest {
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))
        server.start()

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(TodoApiService::class.java)
            val repo = TaskRepository(apiService)
            val vm = TaskViewModel(repo)

            vm.uiState.test {
                assertEquals(TaskUiState.Loading, awaitItem())
                testScheduler.runCurrent()
                val error = awaitItem() as TaskUiState.Error
                assertTrue(error.message.contains("Server Error (500)"))
            }

            val request = server.takeRequest(3, java.util.concurrent.TimeUnit.SECONDS)
            assertNotNull(request)
            assertEquals("/todos", request?.path)
            assertEquals("GET", request?.method)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `actual retrofit timeout integration test verifies server request and produces SocketTimeoutException`() = runTest {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody("[]").setBodyDelay(2, java.util.concurrent.TimeUnit.SECONDS))
        server.start()

        try {
            val okHttpClient = okhttp3.OkHttpClient.Builder()
                .readTimeout(200, java.util.concurrent.TimeUnit.MILLISECONDS)
                .connectTimeout(200, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(TodoApiService::class.java)
            val repo = TaskRepository(apiService)

            // Verify repository directly returns Result.Error with SocketTimeoutException
            val result = repo.getTasks()
            assertTrue(result is Result.Error)
            val errorResult = result as Result.Error
            assertTrue(errorResult.exception is SocketTimeoutException)

            val request = server.takeRequest(3, java.util.concurrent.TimeUnit.SECONDS)
            assertNotNull(request)
            assertEquals("/todos", request?.path)
            assertEquals("GET", request?.method)

            // Verify ViewModel emits Internet Error state using a separate fresh server and repo instance
            val server2 = MockWebServer()
            server2.enqueue(MockResponse().setBody("[]").setBodyDelay(2, java.util.concurrent.TimeUnit.SECONDS))
            server2.start()
            try {
                val retrofit2 = Retrofit.Builder()
                    .baseUrl(server2.url("/"))
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val repo2 = TaskRepository(retrofit2.create(TodoApiService::class.java))
                val vm = TaskViewModel(repo2)

                vm.uiState.test {
                    assertEquals(TaskUiState.Loading, awaitItem())
                    testScheduler.runCurrent()
                    val errorState = awaitItem() as TaskUiState.Error
                    assertTrue(errorState.message.contains("Internet Error"))
                }
                val request2 = server2.takeRequest(3, java.util.concurrent.TimeUnit.SECONDS)
                assertNotNull(request2)
                assertEquals("/todos", request2?.path)
            } finally {
                server2.shutdown()
            }
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `actual retrofit retry integration test makes another request and updates UI successfully`() = runTest {
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(500).setBody("Server Error"))
        server.enqueue(MockResponse().setResponseCode(200).setBody("[{\"userId\": 1, \"id\": 1, \"title\": \"Success After Retry\", \"completed\": false}]"))
        server.start()

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(TodoApiService::class.java)
            val repo = TaskRepository(apiService)
            val vm = TaskViewModel(repo)

            vm.uiState.test {
                assertEquals(TaskUiState.Loading, awaitItem())
                testScheduler.runCurrent()
                val error = awaitItem() as TaskUiState.Error
                assertTrue(error.message.contains("Server Error (500)"))

                vm.fetchTasks(isRefresh = true)

                assertEquals(TaskUiState.Loading, awaitItem())
                testScheduler.runCurrent()
                val success = awaitItem() as TaskUiState.Success
                assertEquals(1, success.tasks.size)
                assertEquals("Success After Retry", success.tasks[0].title)
            }

            assertEquals(2, server.requestCount)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun `newer refresh request supersedes older request and prevents race condition`() = runTest {
        val suspendingApiService = object : TodoApiService {
            var callCount = 0
            override suspend fun getTodos(): List<TodoDto> {
                callCount++
                if (callCount == 1) {
                    kotlinx.coroutines.delay(1000)
                    return listOf(TodoDto(1, 1, "Old Task", false))
                } else {
                    return listOf(TodoDto(1, 2, "New Task", true))
                }
            }
        }
        val repo = TaskRepository(suspendingApiService)
        val vm = TaskViewModel(repo)

        testScheduler.runCurrent()
        vm.fetchTasks(isRefresh = true)
        testScheduler.advanceUntilIdle()

        val currentState = vm.uiState.value
        assertTrue(currentState is TaskUiState.Success)
        val successState = currentState as TaskUiState.Success
        assertEquals(1, successState.tasks.size)
        assertEquals("New Task", successState.tasks[0].title)
    }

    @Test
    fun `repository rethrows CancellationException and does not treat as API error`() = runTest {
        val cancellingService = object : TodoApiService {
            override suspend fun getTodos(): List<TodoDto> {
                throw CancellationException("Cancelled")
            }
        }
        val cancellingRepo = TaskRepository(cancellingService)
        try {
            cancellingRepo.getTasks()
            org.junit.Assert.fail("Expected CancellationException to be thrown")
        } catch (e: CancellationException) {
            assertEquals("Cancelled", e.message)
        }
    }

    @Test
    fun `refresh failure preserves previous task list and shows error message`() = runTest {
        fakeApiService.todos = listOf(TodoDto(1, 1, "Task 1", false))
        viewModel = TaskViewModel(repository)

        viewModel.uiState.test {
            assertEquals(TaskUiState.Loading, awaitItem())
            val success1 = awaitItem() as TaskUiState.Success
            assertEquals(1, success1.tasks.size)

            fakeApiService.exception = IOException("Network down")
            fakeApiService.todos = null
            viewModel.fetchTasks(isRefresh = true)

            val refreshing = awaitItem() as TaskUiState.Success
            assertTrue(refreshing.isRefreshing)

            val successWithWarning = awaitItem() as TaskUiState.Success
            assertEquals(1, successWithWarning.tasks.size)
            assertNotNull(successWithWarning.errorMessage)
            assertTrue(successWithWarning.errorMessage!!.contains("Internet Error"))
        }
    }

    @Test
    fun `HTTP 5xx emits Server Error`() = runTest {
        val response = Response.error<List<TodoDto>>(500, "Server Error".toResponseBody("text/plain".toMediaType()))
        fakeApiService.exception = HttpException(response)
        viewModel = TaskViewModel(repository)

        viewModel.uiState.test {
            assertEquals(TaskUiState.Loading, awaitItem())
            val error = awaitItem() as TaskUiState.Error
            assertTrue(error.message.contains("Server Error (500)"))
        }
    }

    @Test
    fun `HTTP 4xx emits API Request Error`() = runTest {
        val response = Response.error<List<TodoDto>>(404, "Not Found".toResponseBody("text/plain".toMediaType()))
        fakeApiService.exception = HttpException(response)
        viewModel = TaskViewModel(repository)

        viewModel.uiState.test {
            assertEquals(TaskUiState.Loading, awaitItem())
            val error = awaitItem() as TaskUiState.Error
            assertTrue(error.message.contains("API/Request Error (404)"))
        }
    }
}

class FakeTodoApiService : TodoApiService {
    var todos: List<TodoDto>? = null
    var exception: Throwable? = null

    override suspend fun getTodos(): List<TodoDto> {
        exception?.let { throw it }
        return todos ?: emptyList()
    }
}
