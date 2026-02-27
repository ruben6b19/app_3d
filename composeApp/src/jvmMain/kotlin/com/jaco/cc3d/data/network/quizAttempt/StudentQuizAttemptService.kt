package com.jaco.cc3d.data.network.quizAttempt

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Base URL: /api/v1/studentquizattempts
 */
interface StudentQuizAttemptService {

    // 1. INICIAR/CREAR INTENTO (POST /)
    @POST("studentquizattempts")
    suspend fun createAttempt(
        @Body request: CreateAttemptRequest
    ): Response<BackendResponseWrapper<CreateAttemptResponse>>

    // 2. OBTENER TODOS LOS INTENTOS (GET /all/{page})
    // Filtros posibles: scheduledQuizId, studentId, status
    @GET("studentquizattempts/all/{page}")
    suspend fun getAllAttempts(
        @Path("page") page: Int,
        @QueryMap filter: Map<String, String> = emptyMap()
    ): Response<BackendResponseWrapper<PaginationResponse<StudentQuizAttemptDto>>>

    // 3. OBTENER INTENTO POR ID (GET /{attemptId})
    @GET("studentquizattempts/{id}")
    suspend fun getAttemptById(
        @Path("id") attemptId: String
    ): Response<BackendResponseWrapper<StudentQuizAttemptDto>>

    // 4. ACTUALIZAR RESPUESTAS / SUBMIT FINAL (PATCH /{attemptId})
    @PATCH("studentquizattempts/{id}")
    suspend fun updateAttempt(
        @Path("id") attemptId: String,
        @Body request: UpdateAttemptRequest
    ): Response<BackendResponseWrapper<StudentQuizAttemptDto>>

    // 5. ELIMINAR INTENTO (DELETE /{attemptId})
    @DELETE("studentquizattempts/{id}")
    suspend fun deleteAttempt(
        @Path("id") attemptId: String
    ): Response<BackendResponseWrapper<Unit>>
}