package com.jaco.cc3d.domain.repositories.course

import com.jaco.cc3d.data.local.entities.CourseEntity
import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.course.CourseDto
import com.jaco.cc3d.data.network.course.CourseRequest
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.models.CourseDomainRequest
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de Repositorio para la entidad Curso (Course).
 * Define las operaciones CRUD (Create, Read, Update, Delete) que la capa de Dominio
 * espera de la capa de Datos (Data).
 * * NOTA: Esta interfaz maneja los DTOs y Request DTOs de la capa de Datos.
 */
interface CourseRepository {

    /**
     * Crea un nuevo curso en el backend.
     * @param request El DTO de solicitud con los datos del curso.
     * @return Result que contiene el DTO del curso creado, o un error.
     */
    suspend fun createCourse(request: CourseRequest): Result<CourseDto>

    /**
     * Obtiene una lista paginada de todos los cursos.
     * @param page Número de página a obtener (ej. 1).
     * @param limit Límite de ítems por página (ej. 10).
     * @param query Opcional: término de búsqueda para filtrar.
     * @return Result que contiene la respuesta paginada con DTOs de curso, o un error.
     */
    suspend fun getAllCourses(page: Int, filter: Map<String, String>): Result<PaginationResponse<CourseDto>>

    /**
     * Obtiene los detalles de un curso específico por su ID.
     * @param courseId El ID del curso a buscar.
     * @return Result que contiene el DTO del curso, o un error (ej. si no se encuentra).
     */
    suspend fun getCourseById(courseId: String): Result<Course>

    /**
     * Actualiza un curso existente.
     * @param courseId El ID del curso a actualizar.
     * @param request El DTO de solicitud con los datos actualizados.
     * @return Result que contiene el DTO del curso actualizado, o un error.
     */
    suspend fun updateCourse(courseId: String, request: CourseRequest): Result<CourseDto>

    /**
     * Elimina un curso por su ID.
     * @param courseId El ID del curso a eliminar.
     * @return Result<Unit> indicando éxito (Unit) o un error.
     */
    suspend fun deleteCourse(courseId: String): Result<Unit>

    suspend fun getLocalCoursesByTeacher(teacherId: String): List<CourseEntity>
}