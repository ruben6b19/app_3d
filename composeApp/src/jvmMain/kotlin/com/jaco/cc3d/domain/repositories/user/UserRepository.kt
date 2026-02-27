package com.jaco.cc3d.domain.repositories.user

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.user.UserDto
import com.jaco.cc3d.data.network.user.UserRequest
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.domain.models.UserDomainRequest

interface UserRepository {
    suspend fun createUser(request: UserRequest): Result<UserDto>

    // 💡 READ ALL (Filtrado por Instituto)
    suspend fun getUsersByInstitute(
        instituteId: String,
        page: Int,
        limit: Int = 10,
        query: String? = null
    ): Result<PaginationResponse<UserDto>>

    // 💡 READ ONE
    suspend fun getUserById(userId: String): Result<UserDto>

    // 💡 UPDATE
    suspend fun updateUser(userId: String, request: UserRequest): Result<UserDto>

    // 💡 DELETE (Añadido para completar el CRUD)
    suspend fun deleteUser(userId: String): Result<Unit>
}