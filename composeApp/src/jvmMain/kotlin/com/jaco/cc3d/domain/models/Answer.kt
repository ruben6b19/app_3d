package com.jaco.cc3d.domain.models

/**
 * Representa la respuesta de un estudiante a una pregunta específica.
 */
data class Answer(
    val question: QuizQuestion, // Objeto completo de la pregunta
    val studentAnswer: Int?,    // Índice de la opción elegida por el alumno
    val isCorrect: Boolean,     // Resultado de la calificación
    val score: Double           // Puntos obtenidos en esta pregunta
)