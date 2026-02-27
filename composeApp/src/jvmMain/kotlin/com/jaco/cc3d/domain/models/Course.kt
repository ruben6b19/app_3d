package com.jaco.cc3d.domain.models

/**
 * Modelo de Dominio para la entidad Course.
 * Representa un curso existente que se lista o se recupera.
 */
data class Course(
    val id: String, // Usamos 'id' en el dominio

    // Referencias como IDs
    val instituteId: String,
    val contentUrl: String?,
    val subjectId: String,
    val subjectName: String? = null,
    val teacherId: String,

    val students: List<User> = emptyList(),
    // Campos de Curso
    val academicYear: Int,
    val group: String,
    val status: Int,
    val enrolledStudentsCount: Int,

    // Auditoría (Información de sólo lectura)
    val createdBy: String,
    val updatedBy: String?,
    val createdAt: String,
    val updatedAt: String
)