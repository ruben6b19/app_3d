package com.jaco.cc3d.domain.models

/**
 * Modelo de Dominio para la entidad Enrollment (Matriculación).
 * Representa la relación existente entre un estudiante y un curso.
 */
data class Enrollment(
    val id: String, // ID de la matrícula (PK)

    // Referencias
    val studentId: String, // ID del estudiante (FK a User)
    val courseId: String,  // ID del curso (FK a Course)
    val contentUrl: String,
    val subjectName: String,
    val subjectId: String,
    val group: String,
    val academicYear: Int,

    // Campos de Matriculación
    val enrollmentDate: String, // Fecha en formato ISO string
    val status: Int, // 1: activo, 0: inactivo

    // Auditoría (Información de sólo lectura)
    val createdBy: String,
    val updatedBy: String?,
    val createdAt: String,
    val updatedAt: String
)