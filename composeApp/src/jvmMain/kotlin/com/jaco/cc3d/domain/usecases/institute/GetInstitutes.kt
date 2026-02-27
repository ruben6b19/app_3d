package com.jaco.cc3d.domain.usecases.institute

import com.jaco.cc3d.domain.models.Institute
import com.jaco.cc3d.data.repositories.institute.InstituteRepositoryImpl
//import com.jaco.cc3d.domain.repositories.toDomain
import com.jaco.cc3d.data.mappers.toDomainModel
import javax.inject.Inject

/**
 * Use Case para obtener la lista completa de institutos.
 * Se encarga de llamar al Repositorio y mapear los DTOs a modelos de Dominio.
 */
class GetInstitutes @Inject constructor(
    private val repository: InstituteRepositoryImpl
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 10,
        query: String? = null
    ): Result<List<Institute>> {
        return repository.getAllInstitutes(page, limit, query)
            .mapCatching { paginationResponse ->
                // Mapeamos la lista de DTOs a la lista de modelos de Dominio
                paginationResponse.docs.map { it.toDomainModel() }
            }
    }
}