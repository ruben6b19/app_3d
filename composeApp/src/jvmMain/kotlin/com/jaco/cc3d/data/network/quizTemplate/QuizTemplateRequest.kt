package com.jaco.cc3d.data.network.quizTemplate

import kotlinx.serialization.Serializable

/**
 * Data class que representa la estructura mínima para crear/actualizar una plantilla.
 */
@Serializable
data class QuizTemplateRequest(
    val subject: String,
    val name: String,
    val language: String,
    val status: Int? = null // Opcional para PATCH
)
