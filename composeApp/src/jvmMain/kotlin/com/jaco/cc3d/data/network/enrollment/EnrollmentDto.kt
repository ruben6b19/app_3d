package com.jaco.cc3d.data.network.enrollment

import kotlinx.serialization.Serializable

/**
 * Representa un objeto de Matriculación (Enrollment) retornado por el backend.
 * Nota: Los IDs de estudiante y curso son referencias a otras colecciones (User y Course).
 */
@Serializable
data class EnrollmentDto(
    val _id: String,
    val student: String,
    val course: CoursePopulatedDto,
    val enrollmentDate: String,
    val createdBy: String,
    val status: Int,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CoursePopulatedDto(
    val _id: String,
    val subject: SubjectPopulatedDto, // 💡 El populate anidado
    val group: String,
    val academicYear: Int,
)

@Serializable
data class SubjectPopulatedDto(
    val _id: String,
    val name: String,
    val description: String? = null,
    val contentUrl: String?="",
)