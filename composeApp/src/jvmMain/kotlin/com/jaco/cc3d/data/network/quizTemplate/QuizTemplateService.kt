package com.jaco.cc3d.data.network.quizTemplate

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import com.jaco.cc3d.data.network.common.PaginationResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de Retrofit para interactuar con los endpoints de Plantillas de Quiz.
 * Base URL: /api/v1/quiztemplates
 */
interface QuizTemplateService {

    // 1. CREAR PLANTILLA DE QUIZ (POST /)
    @POST("quiztemplates")
    suspend fun createQuizTemplate(
        @Body request: QuizTemplateRequest
    ): Response<BackendResponseWrapper<QuizTemplateDto>>

    // 2. OBTENER TODAS LAS PLANTILLAS (GET /all/{page})
    @GET("quiztemplates/all/{page}")
    suspend fun getAllQuizTemplates(
        @Path("page") page: Int,
        @QueryMap filter: Map<String, String> = emptyMap()
    ): Response<BackendResponseWrapper<PaginationResponse<QuizTemplateDto>>>

    // 3. OBTENER PLANTILLA POR ID (GET /{templateId})
    @GET("quiztemplates/{id}")
    suspend fun getQuizTemplateById(
        @Path("id") templateId: String
    ): Response<BackendResponseWrapper<QuizTemplateDto>>

    // 4. ACTUALIZAR PLANTILLA (PATCH /{templateId})
    @PATCH("quiztemplates/{id}")
    suspend fun updateQuizTemplate(
        @Path("id") templateId: String,
        @Body request: QuizTemplateRequest
    ): Response<BackendResponseWrapper<QuizTemplateDto>>

    // 5. ELIMINAR PLANTILLA (DELETE /{templateId})
    @DELETE("quiztemplates/{id}")
    suspend fun deleteQuizTemplate(
        @Path("id") templateId: String
    ): Response<BackendResponseWrapper<Unit>> // Se usa Unit ya que no se espera cuerpo de respuesta
}


