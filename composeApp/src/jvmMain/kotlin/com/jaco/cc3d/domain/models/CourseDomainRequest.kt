package com.jaco.cc3d.domain.models

/**
 * Modelo de Dominio para la solicitud de creación/actualización de un Curso.
 * Contiene solo los campos modificables que se envían al backend.
 */
data class CourseDomainRequest(
    val instituteId: String,
    val subjectId: String,
    val teacherId: String,
    val academicYear: Int,
    val group: String
)