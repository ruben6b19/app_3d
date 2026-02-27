package com.jaco.cc3d.data.mappers

// DTOs de la Capa de Datos
import com.jaco.cc3d.data.local.entities.EnrollmentEntity
import com.jaco.cc3d.data.network.enrollment.EnrollmentDto
import com.jaco.cc3d.data.network.enrollment.EnrollmentRequest

// Modelos de la Capa de Dominio
import com.jaco.cc3d.domain.models.Enrollment
import com.jaco.cc3d.domain.models.EnrollmentDomainRequest

// =========================================================================
// MAPPERS DE ENROLLMENT (MATRICULACIÓN)
// =========================================================================

/**
 * Mapea [EnrollmentDto] (Data Layer) a [Enrollment] (Domain Layer).
 * Transforma el DTO recibido del backend en un modelo de dominio.
 */
fun EnrollmentDto.toDomainModel(): Enrollment {
    return Enrollment(
        id = _id, // EnrollmentDto usa 'id' para el campo _id de MongoDB
        studentId = student, // ID del estudiante (FK)
        courseId = course._id,   // ID del curso (FK)
        contentUrl = course.subject.contentUrl?:"",
        subjectName = course.subject.name,
        subjectId = course.subject._id,

        group = course.group,
        academicYear = course.academicYear,
        enrollmentDate = enrollmentDate,
        status = status,
        createdBy = createdBy,
        updatedBy = "", // El DTO no tiene updatedBy, asumimos cadena vacía si no existe
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Mapea [EnrollmentDomainRequest] (Domain Layer) a [EnrollmentRequest] (Data Layer).
 * Contiene solo los IDs necesarios para crear una nueva matrícula.
 */
fun EnrollmentDomainRequest.toDataRequest(): EnrollmentRequest {
    return EnrollmentRequest(
        student = this.studentId,
        course = this.courseId
    )
}

/**
 * Mapea el DTO de la red a la Entidad de Room.
 * Se rellena con todos los campos de auditoría y fechas.
 */
fun EnrollmentDto.toEntity(): EnrollmentEntity {
    return EnrollmentEntity(
        id = _id,
        studentId = student,
        courseId = course._id,
        subjectId = course.subject._id,
        subjectName = course.subject.name,
        contentUrl = course.subject.contentUrl ?: "",
        group = course.group,
        academicYear = course.academicYear,
        status = status,
        // --- Campos de fecha y auditoría rellenados ---
        enrollmentDate = enrollmentDate,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Mapea la Entidad de Room al Modelo de Dominio para la UI.
 */
fun EnrollmentEntity.toDomain(): Enrollment {
    return Enrollment(
        id = id,
        studentId = studentId,
        courseId = courseId,
        subjectId = subjectId,
        subjectName = subjectName,
        contentUrl = contentUrl,
        group = group,
        academicYear = academicYear,
        status = status,
        // --- Campos de fecha y auditoría rellenados ---
        enrollmentDate = enrollmentDate,
        createdBy = createdBy,
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        updatedBy = null // Room no suele guardar este si es opcional, o puedes añadirlo a la entidad
    )
}