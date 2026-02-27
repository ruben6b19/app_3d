package com.jaco.cc3d.domain.usecases.enrollment

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.enrollment.EnrollmentRepositoryImpl
import com.jaco.cc3d.domain.models.Enrollment
import com.jaco.cc3d.domain.models.EnrollmentDomainRequest
import javax.inject.Inject

/**
 * Use Case para crear una nueva matriculación.
 * Inscribe un estudiante a un curso.
 */
class CreateEnrollment @Inject constructor(
    private val repository: EnrollmentRepositoryImpl
) {
    /**
     * Invoca el caso de uso para crear una nueva matriculación.
     * @param request El modelo de solicitud de dominio con los IDs de estudiante y curso.
     * @return Result que contiene el modelo de dominio [Enrollment] creado, o un error.
     */
    suspend operator fun invoke(request: EnrollmentDomainRequest): Result<Enrollment> {
        // 1. Mapear el modelo de Dominio a DTO de solicitud
        val enrollmentRequestDto = request.toDataRequest()

        // 2. Llamar al repositorio para crear la matrícula
        return repository.createEnrollment(enrollmentRequestDto)
            .mapCatching { dto ->
                // 3. Mapear el DTO de respuesta a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}