package com.jaco.cc3d.domain.models

/**
 * Objeto de Solicitud de Dominio para la creación/actualización de QuizTemplate.
 * Utilizado para llevar datos de la UI al Repositorio.
 */
data class QuizTemplateDomainRequest(
    val subjectId: String,
    val name: String,
    val language: String,
    val status: Int? = null // Opcional para actualizaciones
)