package com.jaco.cc3d.domain.usecases.quizAttempt

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizAttempt.StudentQuizAttemptRepositoryImpl
import com.jaco.cc3d.domain.models.CreateAttemptResult
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.domain.models.StudentQuizAttempt
import javax.inject.Inject



class CreateAttempt @Inject constructor(
    private val repository: StudentQuizAttemptRepositoryImpl
) {
    suspend operator fun invoke(
        scheduledQuizId: String,
        //studentId: String,
        amount: Int = 10,
        isRandom: Boolean = true
    ): Result<CreateAttemptResult> {
        return repository.createAttempt(scheduledQuizId, amount, isRandom)
            .mapCatching { response ->
                val domainQuestions = response.questions.map { it.toDomainModel() }

                // 2. Mapeamos el intento, pasándole las preguntas para que rellene los datos
                val domainAttempt = response.attempt.toDomainModel(fullQuestions = domainQuestions)
                CreateAttemptResult(
                    attempt = domainAttempt,
                    questions = domainQuestions
                )
            }
    }
}