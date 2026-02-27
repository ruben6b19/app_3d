package com.jaco.cc3d.data.network.apiAuth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class TokenData(
    val accessToken: String,
    val refreshToken: String? = null // Firebase a veces devuelve uno nuevo
)