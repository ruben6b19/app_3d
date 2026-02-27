package com.jaco.cc3d.data.local.entities


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    primaryKeys = ["id", "courseId"],
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE // Si borras el curso, se borran sus alumnos localmente
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class StudentEntity(
    val id: String, // El _id de MongoDB
    val courseId: String,       // Relación con el curso
    val fullName: String,
    val email: String = ""
)