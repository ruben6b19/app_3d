package com.jaco.cc3d.domain.usecases.quizQuestion

import com.jaco.cc3d.data.repositories.quizQuestion.QuizQuestionRepositoryImpl
import javax.inject.Inject

class DeleteQuizQuestion @Inject constructor(
    private val repository: QuizQuestionRepositoryImpl
) {
    /**
     * @param questionId ID de la pregunta a eliminar.
     */
    suspend operator fun invoke(questionId: String): Result<Unit> {
        return repository.deleteQuizQuestion(questionId)
    }
}