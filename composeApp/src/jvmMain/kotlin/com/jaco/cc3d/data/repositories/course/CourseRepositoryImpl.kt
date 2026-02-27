package com.jaco.cc3d.data.repositories.course

import com.jaco.cc3d.data.local.dao.CourseDao
import com.jaco.cc3d.data.local.entities.CourseEntity
import com.jaco.cc3d.data.mappers.toDomain
import com.jaco.cc3d.data.mappers.toEntity
import com.jaco.cc3d.data.mappers.toStudentEntity
import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.course.CourseDto
import com.jaco.cc3d.data.network.course.CourseRequest
import com.jaco.cc3d.data.network.course.CourseService
import com.jaco.cc3d.domain.repositories.course.CourseRepository
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.data.network.utils.bodyOrThrow // Asumimos esta utilidad para manejar el cuerpo de la respuesta
import com.jaco.cc3d.domain.models.Course
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del [CourseRepository].
 * Utiliza [CourseService] para comunicarse con el backend y maneja la lógica de llamadas seguras a la API.
 */
@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseService: CourseService,
    private val courseDao: CourseDao
) : CourseRepository {

    // Función auxiliar para manejar la respuesta segura de la API
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    // --- Implementaciones de las funciones CRUD ---

    override suspend fun createCourse(request: CourseRequest): Result<CourseDto> = apiCall {
        courseService.createCourse(request).bodyOrThrow()
    }

    override suspend fun getAllCourses(page: Int, filter: Map<String, String>): Result<PaginationResponse<CourseDto>> = apiCall {
        val response = courseService.getAllCourses(page, filter).bodyOrThrow()

        val entities = response.docs.map { it.toEntity() }
        courseDao.upsertCourses(entities)

        // 2. Extraemos todos los estudiantes de todos los cursos recibidos
        // flatMap recorre cada curso y "aplana" sus listas de estudiantes en una sola
        val studentEntities = response.docs.flatMap { courseDto ->
            courseDto.students.map { userDto ->
                userDto.toStudentEntity(courseDto._id)
            }
        }

        // 3. Insertamos todos los estudiantes de una sola vez
        if (studentEntities.isNotEmpty()) {
            courseDao.insertStudents(studentEntities)
        }

        response
        //courseService.getAllCourses(page, filter).bodyOrThrow()
    }

    override suspend fun getLocalCoursesByTeacher(teacherId: String): List<CourseEntity> {
        return courseDao.getCoursesByTeacherId(teacherId)
    }

    override suspend fun getCourseById(courseId: String): Result<Course> {
        val relation = courseDao.getCourseWithStudents(courseId)

        return if (relation != null) {
            // Convertimos la entidad de Room al modelo de dominio que usa el ViewModel
            Result.success(relation.toDomain())
        } else {
            Result.failure(Exception("Curso no encontrado en la base de datos local"))
        }
    }

    override suspend fun updateCourse(courseId: String, request: CourseRequest): Result<CourseDto> = apiCall {
        courseService.updateCourse(courseId, request).bodyOrThrow()
    }

    override suspend fun deleteCourse(courseId: String): Result<Unit> = apiCall {
        // bodyOrThrow() verifica el éxito de la respuesta, si es exitosa, se retorna Unit
        courseService.deleteCourse(courseId).bodyOrThrow()
        Unit
    }
}