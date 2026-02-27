package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.network.quizAttempt.AnswerDto
import com.jaco.cc3d.data.network.quizAttempt.StudentQuizAttemptDto
import com.jaco.cc3d.domain.models.Answer
import com.jaco.cc3d.domain.models.QuizAttemptStatus
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.domain.models.StudentQuizAttempt
import java.time.Instant
import java.util.Date

/**
 * Mapea el DTO del intento.
 * @param fullQuestions Opcional: Lista de preguntas completas para "unir" los datos.
 */
fun StudentQuizAttemptDto.toDomainModel(
    fullQuestions: List<QuizQuestion> = emptyList()
): StudentQuizAttempt {
    return StudentQuizAttempt(
        id = this._id,
        scheduledQuiz = this.scheduledQuiz,
        student = this.student,
        // Pasamos la lista de preguntas al mapear cada respuesta
        questionsAnswered = this.questionsAnswered.map { answerDto ->
            answerDto.toDomainModel(fullQuestions.find { it.id == answerDto.question })
        },
        totalScoreObtained = this.totalScoreObtained,
        status = QuizAttemptStatus.fromInt(this.status),
        startTime = parseIsoDate(this.startTime),
        endTime = this.endTime?.let { parseIsoDate(it) }
    )
}

fun AnswerDto.toDomainModel(questionObject: QuizQuestion? = null): Answer {
    return Answer(
        question = questionObject ?: QuizQuestion(
            id = this.question, // ID que viene del backend
            quizTemplateId = "",
            questionText = "Cargando...", // Texto temporal
            questionType = 0,
            options = emptyList(),
            createdBy = "",
            status = 1,
            createdAt = Date(),
            updatedAt = Date()
        ),
        studentAnswer = this.studentAnswer as? Int, // Cast seguro a Int
        isCorrect = this.isCorrect,
        score = this.score
    )
}

/**
 * Utilidad para convertir Strings ISO 8601 del backend (MongoDB) a objetos Date de Java.
 */
private fun parseIsoDate(isoString: String): Date {
    return try {
        Date.from(Instant.parse(isoString))
    } catch (e: Exception) {
        Date() // Fallback a fecha actual en caso de error
    }
}