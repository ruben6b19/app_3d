package com.jaco.cc3d.data.network.quizQuestion

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import com.jaco.cc3d.data.network.common.PaginationResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de Retrofit para interactuar con los endpoints de Preguntas de Quiz.
 * Base URL: /api/v1/quizquestions
 */
interface QuizQuestionService {

    // 1. CREAR PREGUNTA DE QUIZ (POST /)
    @POST("quizquestions")
    suspend fun createQuizQuestion(
        @Body request: QuizQuestionRequest
    ): Response<BackendResponseWrapper<QuizQuestionDto>>

    // 2. OBTENER TODAS LAS PREGUNTAS (GET /all/{page})
    // Se asume que el filtro principal será por 'quizTemplate'
    @GET("quizquestions/all/{page}")
    suspend fun getAllQuizQuestions(
        @Path("page") page: Int,
        @QueryMap filter: Map<String, String> = emptyMap()
    ): Response<BackendResponseWrapper<PaginationResponse<QuizQuestionDto>>>

    // 3. OBTENER PREGUNTA POR ID (GET /{questionId})
    @GET("quizquestions/{id}")
    suspend fun getQuizQuestionById(
        @Path("id") questionId: String
    ): Response<BackendResponseWrapper<QuizQuestionDto>>

    // 4. ACTUALIZAR PREGUNTA (PATCH /{questionId})
    @PATCH("quizquestions/{id}")
    suspend fun updateQuizQuestion(
        @Path("id") questionId: String,
        @Body request: QuizQuestionRequest
    ): Response<BackendResponseWrapper<QuizQuestionDto>>

    // 5. ELIMINAR PREGUNTA (DELETE /{questionId})
    @DELETE("quizquestions/{id}")
    suspend fun deleteQuizQuestion(
        @Path("id") questionId: String
    ): Response<BackendResponseWrapper<Unit>>
}