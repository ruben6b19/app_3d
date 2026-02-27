package com.jaco.cc3d.data.local.dao

import androidx.room.*
import com.jaco.cc3d.data.local.entities.CourseEntity
import com.jaco.cc3d.data.local.entities.StudentEntity
import com.jaco.cc3d.data.local.relations.CourseWithStudents
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    // Obtener todos los cursos (Modo Offline leerá de aquí)
    @Query("SELECT * FROM courses ORDER BY createdAt DESC") // 👈 De más nuevo a más viejo
    suspend fun getAllCourses(): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: String): CourseEntity?

    // Insertar o actualizar (Si el curso ya existe, reemplaza con la info nueva de la API)
    //@Upsert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCourses(courses: List<CourseEntity>)

    @Query("DELETE FROM courses")
    suspend fun clearAllCourses()

    // Opcional: Borrar un curso específico si el alumno se desvincula
    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteCourseById(courseId: String)

    @Query("DELETE FROM courses")
    suspend fun clearAll()

    // Esta consulta devuelve todos los cursos asociados a un profesor específico
    @Query("SELECT * FROM courses WHERE teacherId = :teacherId ORDER BY createdAt DESC")
    suspend fun getCoursesByTeacherId(teacherId: String): List<CourseEntity>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    // Transacción para guardar todo el paquete del curso
    @Transaction
    suspend fun saveFullCourse(course: CourseEntity, students: List<StudentEntity>) {
        insertCourse(course)
        insertStudents(students)
    }

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseWithStudents(courseId: String): CourseWithStudents?
}