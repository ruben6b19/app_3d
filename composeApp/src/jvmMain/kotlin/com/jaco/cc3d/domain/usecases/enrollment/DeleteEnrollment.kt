package com.jaco.cc3d.domain.usecases.enrollment

import com.jaco.cc3d.domain.repositories.enrollment.EnrollmentRepository
import javax.inject.Inject

/**
 * Use Case para eliminar una matriculación por ID.
 * Desmatricula un estudiante de un curso.
 */
class DeleteEnrollment @Inject constructor(
    private val repository: EnrollmentRepository
) {
    /**
     * Invoca el caso de uso para eliminar una matriculación.
     * @param enrollmentId El ID de la matriculación (registro) a eliminar.
     * @return Result<Unit> indicando éxito o fracaso de la operación.
     */
    suspend operator fun invoke(enrollmentId: String): Result<Unit> {
        // Llama directamente al repositorio. El repositorio maneja la lógica de la capa de datos.
        return repository.deleteEnrollment(enrollmentId)
    }
}