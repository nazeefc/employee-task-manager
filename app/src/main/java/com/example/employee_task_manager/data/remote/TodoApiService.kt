package com.example.employee_task_manager.data.remote

import retrofit2.http.GET

interface TodoApiService {
    @GET("todos")
    suspend fun getTodos(): List<TodoDto>
}
