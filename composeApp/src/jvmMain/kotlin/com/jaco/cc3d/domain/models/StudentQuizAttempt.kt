package com.jaco.cc3d.domain.models

import java.util.Date

/**
 * Representa el intento completo de un estudiante al realizar un examen.
 */
data class StudentQuizAttempt(
    val id: String,
    val scheduledQuiz: String,        // ID del Quiz programado
    val student: String,              // ID del usuario/estudiante
    val questionsAnswered: List<Answer>,
    val totalScoreObtained: Double,
    val status: QuizAttemptStatus,    // Usamos un Enum para mayor claridad
    val startTime: Date,
    val endTime: Date? = null
)

/**
 * Representa los posibles estados de un intento.
 */
enum class QuizAttemptStatus(val value: Int) {
    IN_PROGRESS(0),
    FINISHED(1),
    GRADED(2);

    companion object {
        fun fromInt(value: Int) = entries.find { it.value == value } ?: IN_PROGRESS
    }
}