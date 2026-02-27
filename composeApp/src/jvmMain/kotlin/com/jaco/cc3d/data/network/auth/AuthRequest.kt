package com.jaco.cc3d.data.network.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean// Siempre True para obtener el token de ID
)