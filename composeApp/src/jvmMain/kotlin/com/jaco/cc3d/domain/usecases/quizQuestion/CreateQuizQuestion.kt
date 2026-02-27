package com.jaco.cc3d.domain.usecases.quizQuestion

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizQuestion.QuizQuestionRepositoryImpl
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.domain.models.QuizQuestionDomainRequest
import javax.inject.Inject

/**
 * Use Case para crear una nueva pregunta de quiz.
 */
class CreateQuizQuestion @Inject constructor(
    private val repository: QuizQuestionRepositoryImpl
) {
    /**
     * @param request Datos de dominio para la nueva pregunta.
     * @return Result con el modelo [QuizQuestion] creado.
     */
    suspend operator fun invoke(request: QuizQuestionDomainRequest): Result<QuizQuestion> {
        val requestDto = request.toDataRequest()

        return repository.createQuizQuestion(requestDto)
            .mapCatching { dto -> dto.toDomainModel() }
    }
}