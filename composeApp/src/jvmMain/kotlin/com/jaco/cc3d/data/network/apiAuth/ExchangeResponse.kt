package com.jaco.cc3d.data.network.apiAuth

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeResponse(
    val user: UserData,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,      // El timestamp que calculamos en el backend
    val tokenRefreshed: Boolean
)

@Serializable
data class UserData(
    val _id: String, // Mapea mongoDbId
    val email: String,
    val fullName: String,
    val role: List<Int> ,// Mapea el array de roles (0: student, 1: teacher, 2: admin)
    val institute: InstituteData? = null
)
@Serializable
data class InstituteData(
    val _id: String,
    val name: String,
    val language: String? = null
)