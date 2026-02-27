package com.jaco.cc3d.domain.usecases.user

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.domain.models.UserDomainRequest
import com.jaco.cc3d.domain.repositories.user.UserRepository
import javax.inject.Inject

/**
 * Use Case para crear un nuevo usuario.
 */
class CreateUser @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(request: UserDomainRequest): Result<User> {
        val userRequestDto = request.toDataRequest()

        return repository.createUser(userRequestDto)
            .mapCatching { dto ->
                // Mapeamos el DTO de respuesta (el nuevo usuario) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}