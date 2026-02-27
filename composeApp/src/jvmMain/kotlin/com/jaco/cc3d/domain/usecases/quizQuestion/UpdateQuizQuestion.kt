package com.jaco.cc3d.domain.usecases.quizQuestion

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizQuestion.QuizQuestionRepositoryImpl
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.domain.models.QuizQuestionDomainRequest
import javax.inject.Inject

class UpdateQuizQuestion @Inject constructor(
    private val repository: QuizQuestionRepositoryImpl
) {
    suspend operator fun invoke(
        questionId: String,
        request: QuizQuestionDomainRequest
    ): Result<QuizQuestion> {
        val requestDto = request.toDataRequest()

        return repository.updateQuizQuestion(questionId, requestDto)
            .mapCatching { dto -> dto.toDomainModel() }
    }
}