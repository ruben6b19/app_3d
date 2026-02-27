package com.jaco.cc3d.domain.usecases.enrollment

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.enrollment.EnrollmentRepositoryImpl
import com.jaco.cc3d.domain.models.Enrollment
import javax.inject.Inject

/**
 * Use Case para obtener la lista paginada de matriculaciones (estudiantes)
 * para un curso específico.
 */
class GetAllEnrollments @Inject constructor(
    private val repository: EnrollmentRepositoryImpl
) {
    /**
     * Invoca el caso de uso para obtener matriculaciones con paginación y filtro.
     * @param courseId El ID del curso cuyas matriculaciones se desean obtener.
     * @param page El número de página (por defecto 1).
     * @param limit El límite de elementos por página (por defecto 10).
     * @return Result<List<Enrollment>> La lista de modelos de dominio [Enrollment] o un error.
     */
    suspend operator fun invoke(
        courseId: String,
        page: Int = 1,
        limit: Int = 10,
    ): Result<List<Enrollment>> {
        val filtersMap = mutableMapOf<String, String>()
        filtersMap["limit"] = limit.toString()
        filtersMap["courseId"] = courseId
        return repository.getAllEnrollments(page, filtersMap)
            .mapCatching { paginationResponse ->
                // Mapeamos la lista de DTOs (docs) a la lista de modelos de Dominio [Enrollment]
                paginationResponse.docs.map { it.toDomainModel() }
            }
    }
}