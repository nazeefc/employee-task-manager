package com.example.employee_task_manager.data.worker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.employee_task_manager.data.local.AppDatabase
import com.example.employee_task_manager.data.local.TaskDao
import com.example.employee_task_manager.data.local.TaskEntity
import com.example.employee_task_manager.data.remote.TaskSyncApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class TaskSyncWorkerTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var apiService: TaskSyncApiService
    private lateinit var context: Context

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = database.taskDao()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskSyncApiService::class.java)
    }

    @After
    fun tearDown() {
        database.close()
        mockWebServer.shutdown()
    }

    @Test
    fun workerUploadsPendingTaskSuccessfully() = runBlocking {
        val task = TaskEntity(
            clientId = "test-client-1",
            title = "Sync Task",
            description = "Test Desc",
            priority = "High",
            completed = false,
            syncState = 0 // PENDING
        )
        taskDao.insertTask(task)

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{\"clientId\":\"test-client-1\",\"status\":\"success\"}")
        )

        val worker = TestListenableWorkerBuilder<TaskSyncWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: androidx.work.WorkerParameters
                ): androidx.work.ListenableWorker {
                    return TaskSyncWorker(appContext, workerParameters, apiService, database)
                }
            })
            .build()

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.Success::class, result::class)

        val updatedTask = taskDao.observeTasks().first().first()
        assertEquals(2, updatedTask.syncState) // SYNCED
    }

    @Test
    fun workerRetriesOnServer500Error() = runBlocking {
        val task = TaskEntity(
            clientId = "test-client-500",
            title = "Error Task",
            syncState = 0
        )
        taskDao.insertTask(task)

        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val worker = TestListenableWorkerBuilder<TaskSyncWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: androidx.work.WorkerParameters
                ): androidx.work.ListenableWorker {
                    return TaskSyncWorker(appContext, workerParameters, apiService, database)
                }
            })
            .build()

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.Retry::class, result::class)

        val updatedTask = taskDao.observeTasks().first().first()
        assertEquals(0, updatedTask.syncState) // Reset to PENDING for retry
    }

    @Test
    fun workerHandles4xxPermanentFailureWithoutInfiniteRetry() = runBlocking {
        val task = TaskEntity(
            clientId = "test-client-400",
            title = "Bad Task",
            syncState = 0
        )
        taskDao.insertTask(task)

        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val worker = TestListenableWorkerBuilder<TaskSyncWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: androidx.work.WorkerParameters
                ): androidx.work.ListenableWorker {
                    return TaskSyncWorker(appContext, workerParameters, apiService, database)
                }
            })
            .build()

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.Failure::class, result::class)

        val updatedTask = taskDao.observeTasks().first().first()
        assertEquals(3, updatedTask.syncState) // 3 = FAILED / PERMANENT_ERROR
    }

        @Test
        fun workerResetsStuckSyncingTasksToPending() = runBlocking {
            val task = TaskEntity(
                clientId = "stuck-client",
                title = "Stuck Task",
                syncState = 1 // SYNCING (stuck due to crash)
            )
            taskDao.insertTask(task)

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"clientId\":\"stuck-client\",\"status\":\"success\"}")
            )

            val worker = TestListenableWorkerBuilder<TaskSyncWorker>(context)
                .setWorkerFactory(object : androidx.work.WorkerFactory() {
                    override fun createWorker(
                        appContext: Context,
                        workerClassName: String,
                        workerParameters: androidx.work.WorkerParameters
                    ): androidx.work.ListenableWorker {
                        return TaskSyncWorker(appContext, workerParameters, apiService, database)
                    }
                })
                .build()

            val result = worker.doWork()
            assertEquals(ListenableWorker.Result.Success::class, result::class)

            val updatedTask = taskDao.observeTasks().first().first()
            assertEquals(2, updatedTask.syncState) // SYNCED
        }

        @Test
        fun workerHandlesResponseLossAfterServerSaveAndRetriesIdempotently() = runBlocking {
            val task = TaskEntity(
                clientId = "timeout-after-save-uuid-999",
                title = "Timeout Task",
                syncState = 0
            )
            taskDao.insertTask(task)

        val serverStoredRecords = ConcurrentHashMap<String, String>()
        val receivedClientIds = CopyOnWriteArrayList<String>()
        var attemptCount = 0

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val requestBody = request.body.readUtf8()
                val clientId = Gson().fromJson(requestBody, com.example.employee_task_manager.data.remote.TaskUploadRequest::class.java).clientId
                receivedClientIds.add(clientId)
                attemptCount++

                // Store the real request before intentionally dropping the first response.
                serverStoredRecords.putIfAbsent(clientId, requestBody)
                return if (attemptCount == 1) {
                    MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST)
                } else {
                    MockResponse()
                        .setResponseCode(200)
                        .setBody("{\"clientId\":\"$clientId\",\"status\":\"success\"}")
                }
            }
        }

        val worker = TestListenableWorkerBuilder<TaskSyncWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: androidx.work.WorkerParameters
                ): androidx.work.ListenableWorker {
                    return TaskSyncWorker(appContext, workerParameters, apiService, database)
                }
            })
            .build()

        // First attempt stores the task server-side, then loses the HTTP response.
        val firstResult = worker.doWork()
        assertEquals(ListenableWorker.Result.Retry::class, firstResult::class)
        assertEquals(1, serverStoredRecords.size)
        assertEquals(task.clientId, receivedClientIds.single())

        // Retry sends the same clientId; the server's unique-key storage remains one record.
        val retryResult = worker.doWork()
        assertEquals(ListenableWorker.Result.Success::class, retryResult::class)
        assertEquals(listOf(task.clientId, task.clientId), receivedClientIds)
        assertEquals(1, serverStoredRecords.size)
        assertTrue(serverStoredRecords.containsKey(task.clientId))
        assertEquals("Timeout Task", Gson().fromJson(
            serverStoredRecords.getValue(task.clientId),
            com.example.employee_task_manager.data.remote.TaskUploadRequest::class.java
        ).title)

        val updatedTask = taskDao.observeTasks().first().single()
        assertEquals(2, updatedTask.syncState) // SYNCED
    }

    @Test
    fun workerRetriesAfterDisconnectDuringRequestBodyUpload() = runBlocking {
        val task = TaskEntity(
            clientId = "disconnect-during-upload-uuid-888",
            title = "Body Interrupt Task",
            description = "Test request body disconnect",
            priority = "High",
            completed = false,
            syncState = 0 // PENDING
        )
        taskDao.insertTask(task)

        val serverStoredRecords = ConcurrentHashMap<String, String>()
        val receivedClientIds = CopyOnWriteArrayList<String>()
        val disconnectBodyOnPeek = AtomicBoolean(true)
        val disconnectPeekCount = AtomicInteger(0)

        mockWebServer.dispatcher = object : Dispatcher() {
            override fun peek(): MockResponse {
                return if (disconnectBodyOnPeek.get()) {
                    disconnectPeekCount.incrementAndGet()
                    MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_DURING_REQUEST_BODY)
                } else {
                    MockResponse()
                        .setResponseCode(200)
                        .setBody("{\"clientId\":\"disconnect-during-upload-uuid-888\",\"status\":\"success\"}")
                }
            }

            override fun dispatch(request: RecordedRequest): MockResponse {
                val requestBody = request.body.readUtf8()
                val clientId = Gson().fromJson(
                    requestBody,
                    com.example.employee_task_manager.data.remote.TaskUploadRequest::class.java
                ).clientId
                receivedClientIds.add(clientId)
                serverStoredRecords.putIfAbsent(clientId, requestBody)

                return MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"clientId\":\"$clientId\",\"status\":\"success\"}")
            }
        }

        val worker = TestListenableWorkerBuilder<TaskSyncWorker>(context)
            .setWorkerFactory(object : androidx.work.WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: androidx.work.WorkerParameters
                ): androidx.work.ListenableWorker {
                    return TaskSyncWorker(appContext, workerParameters, apiService, database)
                }
            })
            .build()

        // Attempt 1: peek() returns DISCONNECT_DURING_REQUEST_BODY before body is read.
        // MockWebServer closes connection during request body transfer.
        val firstResult = worker.doWork()
        assertEquals(ListenableWorker.Result.Retry::class, firstResult::class)
        assertTrue(disconnectPeekCount.get() > 0)

        // Task returns to syncState = 0 (PENDING) for retry and no server record was saved.
        val pendingTask = taskDao.observeTasks().first().single()
        assertEquals(0, pendingTask.syncState)
        assertTrue(receivedClientIds.isEmpty())
        assertTrue(serverStoredRecords.isEmpty())

        // Prepare dispatcher for Attempt 2: retry should succeed cleanly.
        disconnectBodyOnPeek.set(false)

        // Attempt 2: Successful retry uploading request body and saving task.
        val retryResult = worker.doWork()
        assertEquals(ListenableWorker.Result.Success::class, retryResult::class)

        // Verify task becomes syncState = 2 (SYNCED), original clientId was uploaded, and no duplicates exist.
        val syncedTask = taskDao.observeTasks().first().single()
        assertEquals(2, syncedTask.syncState)
        assertEquals(listOf("disconnect-during-upload-uuid-888"), receivedClientIds)
        assertEquals(1, serverStoredRecords.size)
        assertTrue(serverStoredRecords.containsKey("disconnect-during-upload-uuid-888"))
    }
}
