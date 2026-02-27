package com.jaco.cc3d.data.network.quizQuestion

import kotlinx.serialization.Serializable

/**
 * DTO de Solicitud para Crear o Actualizar una Pregunta de Quiz.
 */
@Serializable
data class QuizQuestionRequest(
    val quizTemplate: String, // Siempre se requiere el ID de la plantilla a la que pertenece
    val questionText: String,
    val questionType: Int,
    val options: List<OptionDto>?,
    val createdBy: String? = null, // Puede ser opcional, el backend lo infiere del token
    val status: Int? = null
)