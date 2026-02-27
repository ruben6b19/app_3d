package com.jaco.cc3d.domain.repositories.quizQuestion

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionDto
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionRequest

/**
 * Interfaz de Repositorio para la entidad Pregunta de Quiz (QuizQuestion).
 * Define las operaciones CRUD que la capa de Dominio espera de la capa de Datos.
 */
interface QuizQuestionRepository {

    /**
     * Crea una nueva pregunta de quiz vinculada a una plantilla.
     * @param request El DTO de solicitud con los datos de la pregunta (texto, tipo, opciones, score).
     * @return Result que contiene el DTO de la pregunta creada, o un error.
     */
    suspend fun createQuizQuestion(request: QuizQuestionRequest): Result<QuizQuestionDto>

    /**
     * Obtiene una lista paginada de preguntas.
     * Generalmente filtrada por el ID de la plantilla (quizTemplate).
     * @param page Número de página a obtener.
     * @param filter Mapa de filtros (ej. "quizTemplate", "questionType", "status").
     * @return Result que contiene la respuesta paginada con DTOs de preguntas, o un error.
     */
    suspend fun getAllQuizQuestions(page: Int, filter: Map<String, String>): Result<PaginationResponse<QuizQuestionDto>>

    /**
     * Obtiene los detalles de una pregunta específica por su ID.
     * @param questionId El ID de la pregunta a buscar.
     * @return Result que contiene el DTO de la pregunta, o un error.
     */
    suspend fun getQuizQuestionById(questionId: String): Result<QuizQuestionDto>

    /**
     * Actualiza el contenido de una pregunta existente.
     * @param questionId El ID de la pregunta a actualizar.
     * @param request El DTO con los datos actualizados.
     * @return Result que contiene el DTO de la pregunta actualizada, o un error.
     */
    suspend fun updateQuizQuestion(questionId: String, request: QuizQuestionRequest): Result<QuizQuestionDto>

    /**
     * Elimina una pregunta de quiz por su ID.
     * @param questionId El ID de la pregunta a eliminar.
     * @return Result<Unit> indicando éxito o error.
     */
    suspend fun deleteQuizQuestion(questionId: String): Result<Unit>
}