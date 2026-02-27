package com.jaco.cc3d.domain.repositories.enrollment

import com.jaco.cc3d.data.network.common.PaginationResponse
// Asumimos que estos DTOs se definieron en el paso anterior
import com.jaco.cc3d.data.network.enrollment.EnrollmentDto
import com.jaco.cc3d.data.network.enrollment.EnrollmentRequest

/**
 * Interfaz de Repositorio para la entidad Matriculación (Enrollment).
 * Define las operaciones CRUD y de listado que la capa de Dominio
 * espera de la capa de Datos para gestionar las inscripciones.
 */
interface EnrollmentRepository {

    /**
     * Crea una nueva matriculación, inscribiendo un estudiante en un curso.
     * @param request El DTO de solicitud con los IDs de estudiante y curso.
     * @return Result que contiene el DTO de la matriculación creada, o un error.
     */
    suspend fun createEnrollment(request: EnrollmentRequest): Result<EnrollmentDto>

    /**
     * Obtiene una lista paginada de todas las matriculaciones para un curso específico.
     * Esta es la vista principal para gestionar los alumnos de un curso.
     * @param page Número de página a obtener (ej. 1).
     * @param query Opcional: término de búsqueda para filtrar estudiantes (nombre/email).
     * @return Result que contiene la respuesta paginada con DTOs de matriculación, o un error.
     */
    suspend fun getAllEnrollments(
        page: Int,
        filter: Map<String, String>
    ): Result<PaginationResponse<EnrollmentDto>>

    /**
     * Obtiene los detalles de una matriculación específica por su ID.
     * @param enrollmentId El ID de la matriculación a buscar.
     * @return Result que contiene el DTO de la matriculación, o un error.
     */
    suspend fun getEnrollmentById(enrollmentId: String): Result<EnrollmentDto>

    /**
     * Elimina una matriculación (desmatricula al estudiante) por su ID.
     * @param enrollmentId El ID de la matriculación a eliminar.
     * @return Result<Unit> indicando éxito (Unit) o un error.
     */
    suspend fun deleteEnrollment(enrollmentId: String): Result<Unit>
}