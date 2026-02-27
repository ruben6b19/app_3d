package com.jaco.cc3d.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.jaco.cc3d.data.local.entities.CourseEntity
import com.jaco.cc3d.data.local.entities.StudentEntity

data class CourseWithStudents(
    @Embedded val course: CourseEntity, // La entidad principal
    @Relation(
        parentColumn = "id",        // ID en CourseEntity
        entityColumn = "courseId"   // ID de referencia en StudentEntity
    )
    val students: List<StudentEntity> // La lista de estudiantes relacionados
)