package com.jaco.cc3d.domain.usecases.user

import com.jaco.cc3d.domain.repositories.user.UserRepository
import javax.inject.Inject

/**
 * Use Case para eliminar un usuario por ID.
 */
class DeleteUser @Inject constructor(
    private val repository: UserRepository
) {
    // Devuelve Result<Unit> ya que la eliminación exitosa generalmente no retorna datos.
    suspend operator fun invoke(userId: String): Result<Unit> {
        return repository.deleteUser(userId)
    }
}