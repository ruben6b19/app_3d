package com.jaco.cc3d.data.network.quizTemplate

import kotlinx.serialization.Serializable

/**
 * Data class que representa la respuesta DTO de una Plantilla de Quiz.
 */
@Serializable
data class QuizTemplateDto(
    val _id: String,
    val subject: String, // Podría ser un DTO de Subject simplificado
    val name: String,
    val language: String,
    val createdBy: String, // Podría ser un DTO de User simplificado
    val status: Int,
    val createdAt: String,
    val updatedAt: String,
    val isAlreadyScheduled: Boolean = false
)