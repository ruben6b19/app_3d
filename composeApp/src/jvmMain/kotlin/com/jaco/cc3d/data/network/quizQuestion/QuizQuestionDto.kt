package com.jaco.cc3d.data.network.quizQuestion


import kotlinx.serialization.Serializable
import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateDto // Si necesitas la referencia poblada

// DTO para las opciones de respuesta (sub-esquema)
@Serializable
data class OptionDto(
    val text: String,
    val isCorrect: Boolean = false
)

/**
 * DTO para la Pregunta de Quiz (lo que se recibe del backend).
 * NOTA: subject, createdBy, quizTemplate se asumen como String (ID)
 * para simplificar el mapeo, evitando problemas de Any/Object.
 */
@Serializable
data class QuizQuestionDto(
    val _id: String,
    val quizTemplate: String, // ID del QuizTemplate
    val questionText: String,
    val questionType: Int = 1,
    val options: List<OptionDto>?, // Puede ser nulo si questionType no lo requiere
    val createdBy: String, // ID del User
    val status: Int = 1,
    val createdAt: String,
    val updatedAt: String
)