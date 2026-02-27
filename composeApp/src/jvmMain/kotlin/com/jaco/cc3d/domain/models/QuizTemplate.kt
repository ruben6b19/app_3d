package com.jaco.cc3d.domain.models

import java.util.Date

/**
 * Modelo de Dominio para una Plantilla de Quiz.
 * Utilizado por la capa de Dominio y Presentación.
 */
data class QuizTemplate(
    val id: String,
    // La materia a la que pertenece (modelo simplificado o DTO)
    val subject: String,
    val name: String,
    val language: String,
    val createdBy: String, // Referencia al usuario que lo creó
    val status: Int,
    val createdAt: Date,
    val updatedAt: Date,

    val isAlreadyScheduled: Boolean = false
)