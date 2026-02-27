package com.jaco.cc3d.domain.repositories.scheduledQuiz

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.scheduledQuiz.ScheduledQuizDto
import com.jaco.cc3d.data.network.scheduledQuiz.ScheduledQuizRequest

/**
 * Interfaz de Repositorio para la entidad Quiz Programado (ScheduledQuiz).
 * Define las operaciones CRUD que la capa de Dominio espera.
 */
interface ScheduledQuizRepository {

    /**
     * Crea/Programa un nuevo quiz para un curso.
     * @param request Datos de la programación (courseId, templateId, fecha, etc).
     * @return Result con el DTO del quiz programado.
     */
    suspend fun createScheduledQuiz(request: ScheduledQuizRequest): Result<ScheduledQuizDto>

    /**
     * Obtiene una lista paginada de quizzes programados.
     * @param page Número de página.
     * @param filter Filtros opcionales (ej: "course" -> "ID_DEL_CURSO", "status" -> "1").
     * @return Result con respuesta paginada de DTOs.
     */
    suspend fun getAllScheduledQuizzes(
        page: Int,
        filter: Map<String, String> = emptyMap()
    ): Result<PaginationResponse<ScheduledQuizDto>>

    /**
     * Obtiene los detalles de un quiz programado específico.
     * @param id ID del ScheduledQuiz.
     */
    suspend fun getScheduledQuizById(id: String): Result<ScheduledQuizDto>

    /**
     * Actualiza la información de un quiz programado (fecha, estado, detalles).
     * @param id ID del ScheduledQuiz a modificar.
     * @param request Datos actualizados.
     */
    suspend fun updateScheduledQuiz(id: String, request: ScheduledQuizRequest): Result<ScheduledQuizDto>

    /**
     * Elimina o cancela un quiz programado.
     * @param id ID del ScheduledQuiz a eliminar.
     */
    suspend fun deleteScheduledQuiz(id: String): Result<Unit>
}