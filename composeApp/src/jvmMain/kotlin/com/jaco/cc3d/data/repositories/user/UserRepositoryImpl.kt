package com.jaco.cc3d.data.repositories.user

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.user.UserDto
import com.jaco.cc3d.data.network.user.UserRequest
import com.jaco.cc3d.data.network.user.UserService
import com.jaco.cc3d.domain.repositories.user.UserRepository
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.data.network.utils.bodyOrThrow // 💡 Importamos la función bodyOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {

    // Función auxiliar para manejar la respuesta segura de la API
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    // ----------------------------------------------------------------------------------
    // IMPLEMENTACIONES DE CRUD (Refactorizado con bodyOrThrow)
    // ----------------------------------------------------------------------------------

    override suspend fun createUser(request: UserRequest): Result<UserDto> = apiCall {
        userService.createUser(request).bodyOrThrow()
    }

    override suspend fun getUsersByInstitute(
        instituteId: String,
        page: Int,
        limit: Int,
        query: String?
    ): Result<PaginationResponse<UserDto>> = apiCall {
        userService.getAllUsersByInstitute(instituteId, page, limit, query).bodyOrThrow()
    }

    override suspend fun getUserById(userId: String): Result<UserDto> = apiCall {
        userService.getUserById(userId).bodyOrThrow()
    }

    override suspend fun updateUser(userId: String, request: UserRequest): Result<UserDto> = apiCall {
        userService.updateUser(userId, request).bodyOrThrow()
    }

    override suspend fun deleteUser(userId: String): Result<Unit> = apiCall {
        // bodyOrThrow verifica que la llamada sea exitosa.
        userService.deleteUser(userId).bodyOrThrow()
        Unit // Retornamos Unit para cumplir con el tipo Result<Unit>
    }
}