package com.jaco.cc3d.data.repositories.quizQuestion

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionDto
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionRequest
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionService
import com.jaco.cc3d.data.network.utils.bodyOrThrow
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.domain.repositories.quizQuestion.QuizQuestionRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del [QuizQuestionRepository].
 * Utiliza [QuizQuestionService] para la comunicación con el backend.
 */
@Singleton
class QuizQuestionRepositoryImpl @Inject constructor(
    private val quizQuestionService: QuizQuestionService
) : QuizQuestionRepository {

    /**
     * Función auxiliar para estandarizar las llamadas a la API usando el wrapper seguro.
     */
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    // --- Operaciones CRUD ---

    override suspend fun createQuizQuestion(request: QuizQuestionRequest): Result<QuizQuestionDto> = apiCall {
        quizQuestionService.createQuizQuestion(request).bodyOrThrow()
    }

    override suspend fun getAllQuizQuestions(
        page: Int,
        filter: Map<String, String>
    ): Result<PaginationResponse<QuizQuestionDto>> = apiCall {
        // En esta llamada es crucial el filtro 'quizTemplate' que vendrá en el Map
        quizQuestionService.getAllQuizQuestions(
            page = page,
            filter = filter
        ).bodyOrThrow()
    }

    override suspend fun getQuizQuestionById(questionId: String): Result<QuizQuestionDto> = apiCall {
        quizQuestionService.getQuizQuestionById(questionId).bodyOrThrow()
    }

    override suspend fun updateQuizQuestion(
        questionId: String,
        request: QuizQuestionRequest
    ): Result<QuizQuestionDto> = apiCall {
        quizQuestionService.updateQuizQuestion(questionId, request).bodyOrThrow()
    }

    override suspend fun deleteQuizQuestion(questionId: String): Result<Unit> = apiCall {
        quizQuestionService.deleteQuizQuestion(questionId).bodyOrThrow()
        Unit // Retorna Unit indicando éxito en la eliminación
    }
}