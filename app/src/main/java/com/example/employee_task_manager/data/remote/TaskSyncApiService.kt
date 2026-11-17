package com.example.employee_task_manager.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class TaskUploadRequest(
    @SerializedName("clientId") val clientId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("priority") val priority: String,
    @SerializedName("completed") val completed: Boolean
)

data class TaskUploadResponse(
    @SerializedName("clientId") val clientId: String,
    @SerializedName("status") val status: String
)

interface TaskSyncApiService {
    @POST("tasks")
    suspend fun uploadTask(@Body request: TaskUploadRequest): Response<TaskUploadResponse>
}
