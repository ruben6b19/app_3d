package com.jaco.cc3d.domain.models

data class ScheduledQuiz(
    val id: String,

    // Referencias como IDs (puedes añadir nombres si tu DTO los trae poblados)
    val courseId: String,
    val quizTemplateId: String,
    val quizTitle: String = "",

    // Datos del examen
    val quizDate: String, // ISO8601 String para fácil manejo de fechas
    val details: String?,
    val status: Int, // 0: pendiente, 1: programado, 2: completado, 3: cancelado

    val userAttempt: UserAttemptInfo? = null,
    // Auditoría
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String
)

data class UserAttemptInfo(
    val hasAttempted: Boolean,
    val status: Int?, // 0: En curso, 1: Finalizado
    val score: Double
)