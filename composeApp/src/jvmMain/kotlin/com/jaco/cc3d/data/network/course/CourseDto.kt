package com.jaco.cc3d.data.network.course

import com.jaco.cc3d.data.network.enrollment.SubjectPopulatedDto
import com.jaco.cc3d.data.network.user.UserDto
import kotlinx.serialization.Serializable

/**
 * Representa la respuesta de la API para un Curso.
 * Las referencias 'subject', 'teacher' y 'createdBy' se mantienen como String (IDs de MongoDB).
 */
@Serializable
data class CourseDto(
    val _id: String,
    // Referencias
    val institute: String,
    //val subject: String, // ID de la Materia (Subject)
    val subject: SubjectPopulatedDto? = null,
    val teacher: String, // ID del Maestro (User)

    val students: List<UserDto> = emptyList(),
    // Campos de Curso
    val academicYear: Int,
    val group: String,
    val status: Int,
    val enrolledStudentsCount: Int = 0,
    // Auditoría
    val createdBy: String, // ID del Usuario que creó
    val updatedBy: String? = null, // ID del Usuario que actualizó (opcional)
    val createdAt: String,
    val updatedAt: String
)