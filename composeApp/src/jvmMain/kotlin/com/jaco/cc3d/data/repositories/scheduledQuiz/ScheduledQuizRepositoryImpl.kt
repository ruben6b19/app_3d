package com.jaco.cc3d.data.repositories.scheduledQuiz


import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.scheduledQuiz.ScheduledQuizDto
import com.jaco.cc3d.data.network.scheduledQuiz.ScheduledQuizRequest
import com.jaco.cc3d.data.network.scheduledQuiz.ScheduledQuizService
import com.jaco.cc3d.domain.repositories.scheduledQuiz.ScheduledQuizRepository
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.data.network.utils.bodyOrThrow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de [ScheduledQuizRepository] que utiliza Retrofit para
 * la comunicación con el servidor.
 */
@Singleton
class ScheduledQuizRepositoryImpl @Inject constructor(
    private val scheduledQuizService: ScheduledQuizService
) : ScheduledQuizRepository {

    // Helper genérico para llamadas seguras (mismo que Course)
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    override suspend fun createScheduledQuiz(request: ScheduledQuizRequest): Result<ScheduledQuizDto> = apiCall {
        scheduledQuizService.createScheduledQuiz(request).bodyOrThrow()
    }

    override suspend fun getAllScheduledQuizzes(
        page: Int,
        filter: Map<String, String>
    ): Result<PaginationResponse<ScheduledQuizDto>> = apiCall {
        scheduledQuizService.getAllScheduledQuizzes(page, filter).bodyOrThrow()
    }

    override suspend fun getScheduledQuizById(id: String): Result<ScheduledQuizDto> = apiCall {
        scheduledQuizService.getScheduledQuizById(id).bodyOrThrow()
    }

    override suspend fun updateScheduledQuiz(
        id: String,
        request: ScheduledQuizRequest
    ): Result<ScheduledQuizDto> = apiCall {
        scheduledQuizService.updateScheduledQuiz(id, request).bodyOrThrow()
    }

    override suspend fun deleteScheduledQuiz(id: String): Result<Unit> = apiCall {
        scheduledQuizService.deleteScheduledQuiz(id).bodyOrThrow()
        Unit
    }
}