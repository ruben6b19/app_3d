package com.jaco.cc3d.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val name: String,
    val academicYear: Int,
    val group: String,
    val teacherId: String,
    val instituteId: String,        // Añadido
    val enrolledStudentsCount: Int, // Añadido
    val contentUrl: String?,        // Añadido (vital para el MD)
    val createdAt: String,
    val lastUpdated: Long = System.currentTimeMillis()
)