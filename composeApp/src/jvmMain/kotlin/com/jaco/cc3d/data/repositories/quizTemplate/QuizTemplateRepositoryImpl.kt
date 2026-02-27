package com.jaco.cc3d.data.repositories.quizTemplate

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateService
import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateDto
import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateRequest
import com.jaco.cc3d.data.network.utils.bodyOrThrow // Asumo esta utilidad
import com.jaco.cc3d.data.network.utils.safeApiCall // Asumo esta utilidad
import com.jaco.cc3d.domain.repositories.quizTemplate.QuizTemplateRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del [QuizTemplateRepository].
 * Utiliza [QuizTemplateService] para comunicarse con el backend y maneja la lógica de llamadas seguras a la API.
 */
@Singleton
class QuizTemplateRepositoryImpl @Inject constructor(
    private val quizTemplateService: QuizTemplateService
) : QuizTemplateRepository {

    /**
     * Función auxiliar para manejar la respuesta segura de la API y capturar el cuerpo de la respuesta.
     */
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    // --- Implementaciones de las funciones CRUD ---

    override suspend fun createQuizTemplate(request: QuizTemplateRequest): Result<QuizTemplateDto> = apiCall {
        quizTemplateService.createQuizTemplate(request).bodyOrThrow()
    }

    override suspend fun getAllQuizTemplates(
        page: Int,
        filter: Map<String, String>
    ): Result<PaginationResponse<QuizTemplateDto>> = apiCall {

        quizTemplateService.getAllQuizTemplates(
            page = page, filter=filter
        ).bodyOrThrow()
    }

    override suspend fun getQuizTemplateById(templateId: String): Result<QuizTemplateDto> = apiCall {
        quizTemplateService.getQuizTemplateById(templateId).bodyOrThrow()
    }

    override suspend fun updateQuizTemplate(templateId: String, request: QuizTemplateRequest): Result<QuizTemplateDto> = apiCall {
        // Nota: El DTO de solicitud (QuizTemplateRequest) debe ser reutilizable para PUT/PATCH
        // y Retrofit solo envía los campos presentes.
        quizTemplateService.updateQuizTemplate(templateId, request).bodyOrThrow()
    }

    override suspend fun deleteQuizTemplate(templateId: String): Result<Unit> = apiCall {
        // bodyOrThrow() verifica el éxito de la respuesta (200-204).
        // Si tiene éxito, el resultado envuelve a Unit.
        quizTemplateService.deleteQuizTemplate(templateId).bodyOrThrow()
        Unit // Retorna Unit si la llamada fue exitosa.
    }
}