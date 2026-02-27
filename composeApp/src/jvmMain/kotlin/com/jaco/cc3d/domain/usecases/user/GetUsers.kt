package com.jaco.cc3d.domain.usecases.user

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.domain.repositories.user.UserRepository
import javax.inject.Inject
import com.jaco.cc3d.data.mappers.toDomainModel

/**
 * Use Case para obtener la lista de usuarios.
 * Recibe el ID del instituto al que pertenecen los usuarios.
 */
class GetUsers @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        instituteId: String,
        page: Int = 1,
        limit: Int = 10,
        query: String? = null
    ): Result<List<User>> {
        return repository.getUsersByInstitute(instituteId, page, limit, query)
            .mapCatching { paginationResponse ->
                // Mapeamos la lista de DTOs a la lista de modelos de Dominio
                paginationResponse.docs.map { it.toDomainModel() }
            }
    }
}