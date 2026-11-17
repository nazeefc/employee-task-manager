package com.example.employee_task_manager.data.remote

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.util.concurrent.ConcurrentHashMap

class LocalMockTaskServer(private val port: Int = 8080) {

    private val server = MockWebServer()
    private val storedTasks = ConcurrentHashMap<String, String>()

    fun start() {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path ?: ""
                val method = request.method ?: ""

                return when {
                    path.endsWith("/health") -> {
                        MockResponse().setResponseCode(200).setBody("{\"status\":\"UP\"}")
                    }
                    path.endsWith("/tasks") && method.equals("GET", ignoreCase = true) -> {
                        val tasksJson = "[" + storedTasks.values.joinToString(",") + "]"
                        MockResponse().setResponseCode(200).setBody(tasksJson)
                    }
                    path.endsWith("/tasks") && method.equals("POST", ignoreCase = true) -> {
                        val body = request.body.readUtf8()
                        val clientId = extractClientId(body)
                        if (clientId != null) {
                            // Idempotency: store task by clientId as unique key
                            storedTasks.putIfAbsent(clientId, body)
                            MockResponse().setResponseCode(200).setBody("{\"clientId\":\"$clientId\",\"status\":\"success\"}")
                        } else {
                            MockResponse().setResponseCode(400).setBody("{\"error\":\"Missing clientId\"}")
                        }
                    }
                    else -> {
                        MockResponse().setResponseCode(404).setBody("{\"error\":\"Not Found\"}")
                    }
                }
            }
        }
        server.start(port)
        println("==================================================")
        println("LocalMockTaskServer is running on port $port")
        println("Accessible to emulator/phone at: http://127.0.0.1:$port/")
        println("Endpoints:")
        println("  GET  http://localhost:$port/health")
        println("  POST http://localhost:$port/tasks")
        println("  GET  http://localhost:$port/tasks")
        println("==================================================")
    }

    fun stop() {
        server.shutdown()
        println("LocalMockTaskServer stopped.")
    }

    private fun extractClientId(json: String): String? {
        val regex = """"clientId"\s*:\s*"([^"]+)"""".toRegex()
        return regex.find(json)?.groupValues?.get(1)
    }
}

fun main() {
    val server = LocalMockTaskServer(8080)
    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop()
    })
    server.start()
    println("Server is running. Press Ctrl+C to stop...")
    Thread.currentThread().join()
}
