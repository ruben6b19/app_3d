package com.jaco.cc3d.data.repositories.quizAttempt

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.quizAttempt.*
import com.jaco.cc3d.data.network.utils.bodyOrThrow
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.domain.repositories.quizAttempt.StudentQuizAttemptRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de [StudentQuizAttemptRepository] siguiendo el estándar de red del proyecto.
 */
@Singleton
class StudentQuizAttemptRepositoryImpl @Inject constructor(
    private val attemptService: StudentQuizAttemptService
) : StudentQuizAttemptRepository {

    /**
     * Función auxiliar para estandarizar las llamadas a la API usando el wrapper seguro.
     */
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    override suspend fun createAttempt(
        scheduledQuizId: String,
        amount: Int,
        isRandom: Boolean
    ): Result<CreateAttemptResponse> = apiCall {
        val request = CreateAttemptRequest(
            scheduledQuizId = scheduledQuizId,
            amount = amount,
            isRandom = isRandom
        )
        attemptService.createAttempt(request).bodyOrThrow()
    }

    override suspend fun getAllAttempts(
        page: Int,
        filter: Map<String, String>
    ): Result<PaginationResponse<StudentQuizAttemptDto>> = apiCall {
        attemptService.getAllAttempts(page = page, filter = filter).bodyOrThrow()
    }

    override suspend fun getAttemptById(attemptId: String): Result<StudentQuizAttemptDto> = apiCall {
        attemptService.getAttemptById(attemptId).bodyOrThrow()
    }

    override suspend fun updateAttempt(
        attemptId: String,
        answers: Map<String, Int>,
        isFinalSubmit: Boolean
    ): Result<StudentQuizAttemptDto> = apiCall {
        val request = UpdateAttemptRequest(
            answers = answers,
            isFinalSubmit = isFinalSubmit
        )
        attemptService.updateAttempt(attemptId, request).bodyOrThrow()
    }

    override suspend fun deleteAttempt(attemptId: String): Result<Unit> = apiCall {
        attemptService.deleteAttempt(attemptId).bodyOrThrow()
        Unit
    }
}