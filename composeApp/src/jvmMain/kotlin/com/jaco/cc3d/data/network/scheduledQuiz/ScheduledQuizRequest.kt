package com.jaco.cc3d.data.network.scheduledQuiz

import kotlinx.serialization.Serializable

@Serializable
data class ScheduledQuizRequest(
    val course: String,
    val quizTemplate: String,
    val quizDate: String, // Se recomienda enviar ISO8601 String
    val details: String? = null,
    val status: Int? = 1
)