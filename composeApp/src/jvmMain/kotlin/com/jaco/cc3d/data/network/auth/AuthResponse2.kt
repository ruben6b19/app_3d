package com.jaco.cc3d.data.network.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse2(
    val kind: String,
    val idToken: String, // El token de ID de Firebase
    val refreshToken: String? = null,
    val expiresIn: String? = null,
    val isNewUser: Boolean // El ID del usuario en Firebase
)