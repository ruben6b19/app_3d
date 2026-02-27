package com.jaco.cc3d.domain.models

/**
 * Modelo de Dominio para la solicitud de creación de una Matriculación.
 * Contiene solo los campos requeridos para inscribir un estudiante a un curso.
 */
data class EnrollmentDomainRequest(
    val studentId: String, // ID del estudiante a inscribir
    val courseId: String   // ID del curso
)