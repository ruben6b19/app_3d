package com.jaco.cc3d.data.repositories.enrollment

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.enrollment.EnrollmentService
import com.jaco.cc3d.data.network.enrollment.EnrollmentDto
import com.jaco.cc3d.data.network.enrollment.EnrollmentRequest
import com.jaco.cc3d.data.network.utils.bodyOrThrow
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.domain.repositories.enrollment.EnrollmentRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación concreta del [EnrollmentRepository].
 * Utiliza [EnrollmentService] para comunicarse con el backend y maneja la lógica de llamadas seguras a la API.
 */
@Singleton
class EnrollmentRepositoryImpl @Inject constructor(
    private val enrollmentService: EnrollmentService
) : EnrollmentRepository {

    /**
     * Función auxiliar para manejar la respuesta segura de la API y capturar el cuerpo de la respuesta.
     */
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    // --- Implementaciones de las funciones CRUD ---

    override suspend fun createEnrollment(request: EnrollmentRequest): Result<EnrollmentDto> = apiCall {
        enrollmentService.createEnrollment(request).bodyOrThrow()
    }

    override suspend fun getAllEnrollments(
        page: Int,
        filter: Map<String, String>
    ): Result<PaginationResponse<EnrollmentDto>> = apiCall {
        enrollmentService.getAllEnrollments( page,  filter).bodyOrThrow()
    }

    override suspend fun getEnrollmentById(enrollmentId: String): Result<EnrollmentDto> = apiCall {
        enrollmentService.getEnrollmentById(enrollmentId).bodyOrThrow()
    }

    override suspend fun deleteEnrollment(enrollmentId: String): Result<Unit> = apiCall {
        // bodyOrThrow() verifica el éxito de la respuesta, si es exitosa, se retorna Unit
        enrollmentService.deleteEnrollment(enrollmentId).bodyOrThrow()
        Unit // Retorna Unit si la llamada fue exitosa.
    }
}