package com.jaco.cc3d.domain.usecases.quizAttempt

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizAttempt.StudentQuizAttemptRepositoryImpl
import com.jaco.cc3d.domain.models.StudentQuizAttempt
import javax.inject.Inject

class SubmitQuiz @Inject constructor(
    private val repository: StudentQuizAttemptRepositoryImpl
) {
    /**
     * @param attemptId ID del intento activo
     * @param answers Mapa de ID_Pregunta -> Índice_Respuesta
     */
    suspend operator fun invoke(
        attemptId: String,
        answers: Map<String, Int>
    ): Result<StudentQuizAttempt> {
        return repository.updateAttempt(
            attemptId = attemptId,
            answers = answers,
            isFinalSubmit = true
        ).mapCatching { it.toDomainModel() }
    }
}