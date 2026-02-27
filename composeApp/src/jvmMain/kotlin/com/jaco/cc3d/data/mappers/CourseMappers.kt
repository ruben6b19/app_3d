package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.local.entities.CourseEntity
import com.jaco.cc3d.data.local.entities.StudentEntity
import com.jaco.cc3d.data.local.relations.CourseWithStudents
import com.jaco.cc3d.data.network.course.CourseDto
import com.jaco.cc3d.data.network.course.CourseRequest
import com.jaco.cc3d.data.network.user.UserDto
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.domain.models.CourseDomainRequest


// =========================================================================
// MAPPERS DE COURSE (CURSO)
// =========================================================================

/**
 * Mapea [CourseDto] (Data Layer) a [Course] (Domain Layer).
 * Asume que CourseDto tiene un campo _id.
 */
fun CourseDto.toDomainModel(): Course {


    return Course(
        id = _id,
        instituteId = institute,
        subjectId = subject?._id ?: "",
        subjectName = subject?.name,
        contentUrl = subject?.contentUrl ?:"",
        students = this.students.map { it.toDomainModel() },
        teacherId = teacher,
        academicYear = academicYear,
        group = group,
        status = status,
        enrolledStudentsCount = enrolledStudentsCount,
        createdBy = createdBy,
        updatedBy = updatedBy,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Mapea [CourseDomainRequest] (Domain Layer) a [CourseRequest] (Data Layer).
 * Contiene solo los campos modificables.
 */
fun CourseDomainRequest.toDataRequest(): CourseRequest {
    return CourseRequest(
        institute = this.instituteId,
        subject = this.subjectId,
        teacher = this.teacherId,
        academicYear = this.academicYear,
        group = this.group
    )
}

fun CourseWithStudents.toDomain(): Course {
    return Course(
        id = this.course.id,
        instituteId = this.course.instituteId,
        contentUrl = this.course.contentUrl,
        subjectId = this.course.subjectId,
        // 🎯 En el Entity se llama 'name', en el Dominio 'subjectName'
        subjectName = this.course.name,
        teacherId = this.course.teacherId,

        // 🎯 Mapeo de la lista de estudiantes (StudentEntity -> User)
        students = this.students.map { studentEntity ->
            User(
                id = studentEntity.id,
                fullName = studentEntity.fullName,
                email = studentEntity.email,
                firebaseUid = null,
                role = listOf(0), // 0: student
                status = 1,       // 1: active
                instituteId = "", // Dato no persistido en StudentEntity
                createdAt = "",
                updatedAt = ""
            )
        },

        academicYear = this.course.academicYear,
        group = this.course.group,
        enrolledStudentsCount = this.course.enrolledStudentsCount,

        // Campos de Auditoría y Estado (Valores por defecto para Room)
        status = 1,
        createdBy = "",
        updatedBy = null,
        createdAt = this.course.createdAt,
        updatedAt = ""
    )
}

fun UserDto.toStudentEntity(courseId: String): StudentEntity {
    return StudentEntity(
        id = this._id,
        courseId = courseId,
        fullName = this.fullName,
        email = this.email
    )
}

fun CourseDto.toEntity(): CourseEntity {
    return CourseEntity(
        id = this._id,
        subjectId = this.subject?._id ?: "",
        name = this.subject?.name ?: "Sin nombre",
        academicYear = this.academicYear,
        group = this.group,
        teacherId = this.teacher,
        instituteId = this.institute,
        enrolledStudentsCount = this.enrolledStudentsCount,
        contentUrl = this.subject?.contentUrl, // Asumiendo que viene en el subject poblado
        createdAt = this.createdAt,
    )
}

fun CourseEntity.toDomain(): Course {
    return Course(
        id = this.id,
        subjectId = this.subjectId,
        subjectName = this.name,
        academicYear = this.academicYear,
        group = this.group,
        teacherId = this.teacherId,
        instituteId = this.instituteId,
        enrolledStudentsCount = this.enrolledStudentsCount,
        contentUrl = this.contentUrl,

        // Campos de Auditoría con valores por defecto para evitar errores de compilación
        status = 1,
        createdBy = "",
        updatedBy = null,
        createdAt = this.createdAt,
        updatedAt = ""
    )
}