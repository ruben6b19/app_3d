package com.jaco.cc3d.data.network.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenRefreshRequest(
    @SerialName("grant_type")
    val grantType: String, // Siempre debe ser "refresh_token"
    @SerialName("refresh_token")
    val refreshToken: String
)