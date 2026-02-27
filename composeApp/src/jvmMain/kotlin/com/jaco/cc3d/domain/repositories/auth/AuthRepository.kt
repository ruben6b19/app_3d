package com.jaco.cc3d.domain.repositories.auth

import com.jaco.cc3d.data.network.auth.AuthResponse
import com.jaco.cc3d.data.network.auth.TokenRefreshResponse

interface AuthRepository {
    suspend fun signInWithPassword(email: String, password: String): AuthResponse
    suspend fun registerUser(email: String, password: String)

    suspend fun signInWithCustomToken(token: String): String

    suspend fun forceTokenRefresh(refreshToken: String): TokenRefreshResponse
}