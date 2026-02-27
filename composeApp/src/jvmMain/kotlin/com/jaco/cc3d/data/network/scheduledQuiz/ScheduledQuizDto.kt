package com.jaco.cc3d.data.network.scheduledQuiz

import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateDto
import kotlinx.serialization.Serializable

// Objeto que recibimos del Backend
@Serializable
data class ScheduledQuizDto(
    val _id: String,
    val course: String,       // Puede ser ID o DTO según tu populate
    val quizTemplate: String, // Puede ser ID o DTO según tu populate
    val quizTemplateData: QuizTemplateDto? = null,
    val quizDate: String,
    val details: String?,
    val createdBy: String,
    val status: Int,
    val createdAt: String,
    val updatedAt: String,
    val userAttempt: UserAttemptDto? = null
)
