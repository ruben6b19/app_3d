package com.jaco.cc3d.data.network.enrollment

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.course.CourseDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de Retrofit para interactuar con los endpoints de Matriculación (Enrollment).
 */
interface EnrollmentService {

    /**
     * Crea una nueva matriculación (inscribe un estudiante a un curso).
     * Endpoint: POST /enrollments
     */
    @POST("enrollments")
    suspend fun createEnrollment(
        @Body request: EnrollmentRequest
    ): Response<BackendResponseWrapper<EnrollmentDto>>

    /**
     * Obtiene una lista paginada de matriculaciones para un CURSO específico.
     * Este es el caso de uso principal al gestionar los alumnos de un curso.
     * Endpoint: GET /enrollments/course/{courseId}/{page}
     */
    @GET("enrollments/all/{page}")
    suspend fun getAllEnrollments(
        @Path("page") page: Int,
        @QueryMap filter: Map<String, String> = emptyMap()
    ): Response<BackendResponseWrapper<PaginationResponse<EnrollmentDto>>>
    /**
     * Obtiene una matriculación por su ID.
     * Endpoint: GET /enrollments/{id}
     */
    @GET("enrollments/{id}")
    suspend fun getEnrollmentById(
        @Path("id") enrollmentId: String
    ): Response<BackendResponseWrapper<EnrollmentDto>>

    // Nota: Aunque el PATCH para deshabilitar o activar la matrícula es común,
    // mantendremos los métodos de la interfaz SubjectService como base.

    /**
     * Elimina (desmatricula permanentemente) una matriculación.
     * Endpoint: DELETE /enrollments/{id}
     */
    @DELETE("enrollments/{id}")
    suspend fun deleteEnrollment(
        @Path("id") enrollmentId: String
    ): Response<BackendResponseWrapper<Unit>> // Retorna Unit si el backend solo devuelve éxito/error
}