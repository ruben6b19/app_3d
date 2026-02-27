package com.jaco.cc3d.data.network.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Respuesta del endpoint de renovación de tokens (securetoken.googleapis.com/v1/token).
 */
@Serializable
data class TokenRefreshResponse(
    @SerialName("expires_in")
    val expiresIn: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("refresh_token")
    val refreshToken: String, // El refresh token puede cambiar
    @SerialName("id_token")
    val idToken: String, // El nuevo ID Token
    @SerialName("user_id")
    val userId: String,
    @SerialName("project_id")
    val projectId: String
)