package com.jaco.cc3d.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey
    val id: String,          // El _id de MongoDB
    val studentId: String,   // ID del alumno para filtrar
    val courseId: String,
    val subjectId: String,
    val subjectName: String,
    val contentUrl: String,
    val group: String,
    val academicYear: Int,
    val status: Int,
    val enrollmentDate: String,
    val createdBy: String,
    val createdAt: String?,
    val updatedAt: String?
)