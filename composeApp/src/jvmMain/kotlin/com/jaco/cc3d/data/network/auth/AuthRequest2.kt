package com.jaco.cc3d.data.network.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest2(
    val token: String,
    val returnSecureToken: Boolean// Siempre True para obtener el token de ID
)