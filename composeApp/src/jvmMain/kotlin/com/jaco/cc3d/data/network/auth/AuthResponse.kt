package com.jaco.cc3d.data.network.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val idToken: String, // El token de ID de Firebase
    val email: String,
    val refreshToken: String? = null,
    val expiresIn: String? = null,
    val localId: String // El ID del usuario en Firebase
)