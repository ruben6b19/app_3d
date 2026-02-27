package com.jaco.cc3d.domain.repositories.quizAttempt

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.quizAttempt.*

interface StudentQuizAttemptRepository {

    /**
     * Inicia un nuevo intento de examen o recupera uno activo.
     * Devuelve tanto el intento como la lista de preguntas completas.
     */
    suspend fun createAttempt(
        scheduledQuizId: String,
        amount: Int,
        isRandom: Boolean
    ): Result<CreateAttemptResponse>

    /**
     * Obtiene una lista paginada de intentos, útil para el historial del alumno.
     */
    suspend fun getAllAttempts(
        page: Int,
        filter: Map<String, String>
    ): Result<PaginationResponse<StudentQuizAttemptDto>>

    /**
     * Envía las respuestas y finaliza el examen (calificación).
     */
    suspend fun updateAttempt(
        attemptId: String,
        answers: Map<String, Int>,
        isFinalSubmit: Boolean
    ): Result<StudentQuizAttemptDto>

    /**
     * Obtiene un intento específico por ID.
     */
    suspend fun getAttemptById(attemptId: String): Result<StudentQuizAttemptDto>

    suspend fun deleteAttempt(attemptId: String): Result<Unit>
}