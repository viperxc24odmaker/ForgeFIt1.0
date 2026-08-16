package com.makeforge.forgefit.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenRouterApiService {

    @Headers("Content-Type: application/json")
    @POST("api/v1/chat/completions")
    suspend fun chat(@Body request: OpenRouterRequest): OpenRouterResponse
}

data class OpenRouterRequest(
    val model: String = "google/gemini-2.5-flash",
    val messages: List<OpenRouterMessage>,
    val max_tokens: Int = 2048
)

data class OpenRouterMessage(
    val role: String,
    val content: String
)

data class OpenRouterResponse(
    val choices: List<OpenRouterChoice>
)

data class OpenRouterChoice(
    val message: OpenRouterMessage
)
