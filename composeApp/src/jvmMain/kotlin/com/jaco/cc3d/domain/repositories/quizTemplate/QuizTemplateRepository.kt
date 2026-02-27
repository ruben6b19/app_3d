package com.jaco.cc3d.domain.repositories.quizTemplate

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateDto
import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateRequest

/**
 * Interfaz de Repositorio para la entidad Plantilla de Quiz (QuizTemplate).
 * Define las operaciones CRUD que la capa de Dominio espera de la capa de Datos.
 */
interface QuizTemplateRepository {

    /**
     * Crea una nueva plantilla de quiz en el backend.
     * @param request El DTO de solicitud con los datos de la plantilla.
     * @return Result que contiene el DTO de la plantilla creada, o un error.
     */
    suspend fun createQuizTemplate(request: QuizTemplateRequest): Result<QuizTemplateDto>

    /**
     * Obtiene una lista paginada de todas las plantillas de quiz.
     * @param page Número de página a obtener.
     * @param filter Mapa de filtros (ej. "subjectId", "category", "status").
     * @return Result que contiene la respuesta paginada con DTOs, o un error.
     */
    suspend fun getAllQuizTemplates(page: Int, filter: Map<String, String>): Result<PaginationResponse<QuizTemplateDto>>

    /**
     * Obtiene los detalles de una plantilla de quiz específica por su ID.
     * @param templateId El ID de la plantilla a buscar.
     * @return Result que contiene el DTO de la plantilla, o un error.
     */
    suspend fun getQuizTemplateById(templateId: String): Result<QuizTemplateDto>

    /**
     * Actualiza una plantilla de quiz existente.
     * @param templateId El ID de la plantilla a actualizar.
     * @param request El DTO de solicitud con los datos actualizados.
     * @return Result que contiene el DTO de la plantilla actualizada, o un error.
     */
    suspend fun updateQuizTemplate(templateId: String, request: QuizTemplateRequest): Result<QuizTemplateDto>

    /**
     * Elimina una plantilla de quiz por su ID.
     * @param templateId El ID de la plantilla a eliminar.
     * @return Result<Unit> indicando éxito (Unit) o un error.
     */
    suspend fun deleteQuizTemplate(templateId: String): Result<Unit>
}