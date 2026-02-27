package com.jaco.cc3d.domain.models

import java.util.Date

// Modelo para la entidad completa
data class QuizQuestion(
    val id: String,
    val quizTemplateId: String,
    val questionText: String,
    val questionType: Int,
    val options: List<QuizOption>,
    val createdBy: String,
    val status: Int,
    val createdAt: Date,
    val updatedAt: Date
)

// Modelo para las opciones de respuesta
data class QuizOption(
    val text: String,
    val isCorrect: Boolean
)