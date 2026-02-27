package com.jaco.cc3d.domain.models

// Modelo para las solicitudes de creación/edición
data class QuizQuestionDomainRequest(
    val quizTemplateId: String,
    val questionText: String,
    val questionType: Int,
    val options: List<QuizOption>,
    val status: Int? = null
)