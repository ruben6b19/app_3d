package com.jaco.cc3d.data.network.scheduledQuiz

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import com.jaco.cc3d.data.network.common.PaginationResponse
import retrofit2.Response
import retrofit2.http.*

interface ScheduledQuizService {

    // --- 1. CREATE: Programar un nuevo quiz ---
    @POST("scheduled-quizzes/")
    suspend fun createScheduledQuiz(
        @Body request: ScheduledQuizRequest
    ): Response<BackendResponseWrapper<ScheduledQuizDto>>

    // --- 2. READ ALL: Obtener quizzes programados (paginado) ---
    // Filtros comunes: courseId, quizTemplateId, status
    @GET("scheduled-quizzes/all/{page}")
    suspend fun getAllScheduledQuizzes(
        @Path("page") page: Int,
        @QueryMap filters: Map<String, String> = emptyMap()
    ): Response<BackendResponseWrapper<PaginationResponse<ScheduledQuizDto>>>

    // --- 3. READ ONE: Obtener por ID ---
    @GET("scheduled-quizzes/{scheduledQuizId}")
    suspend fun getScheduledQuizById(
        @Path("scheduledQuizId") id: String
    ): Response<BackendResponseWrapper<ScheduledQuizDto>>

    // --- 4. UPDATE: Editar fecha, detalles o estado ---
    @PATCH("scheduled-quizzes/{scheduledQuizId}")
    suspend fun updateScheduledQuiz(
        @Path("scheduledQuizId") id: String,
        @Body request: ScheduledQuizRequest
    ): Response<BackendResponseWrapper<ScheduledQuizDto>>

    // --- 5. DELETE: Cancelar/Eliminar programación ---
    @DELETE("scheduled-quizzes/{scheduledQuizId}")
    suspend fun deleteScheduledQuiz(
        @Path("scheduledQuizId") id: String
    ): Response<BackendResponseWrapper<Unit>>
}