package com.jaco.cc3d.data.network.quizAttempt
import kotlinx.serialization.Serializable

@Serializable
data class StudentQuizAttemptDto(
    val _id: String,
    val scheduledQuiz: String, // ID del quiz programado
    val student: String,       // ID del estudiante
    val questionsAnswered: List<AnswerDto>,
    val totalScoreObtained: Double,
    val status: Int, // 0: En curso, 1: Finalizado
    val startTime: String,
    val endTime: String? = null
)

@Serializable
data class AnswerDto(
    val question: String,        // ID de la pregunta (o QuizQuestionDto si usas populate)
    val studentAnswer: Int?,     // Índice de la opción seleccionada
    val isCorrect: Boolean = false,
    val score: Double = 0.0
)