package com.jaco.cc3d.data.network.auth

import com.jaco.cc3d.data.network.auth.AuthRequest
import com.jaco.cc3d.data.network.auth.AuthRequest2
import com.jaco.cc3d.data.network.auth.TokenRefreshRequest
import com.jaco.cc3d.data.network.auth.AuthResponse
import com.jaco.cc3d.data.network.auth.AuthResponse2
import com.jaco.cc3d.data.network.auth.TokenRefreshResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {

    // Endpoint para iniciar sesión con Correo y Contraseña
    @POST("v1/accounts:signInWithPassword")
    suspend fun signInWithPassword(
        @Query("key") apiKey: String, // Tu clave API
        @Body request: AuthRequest
    ): Response<AuthResponse>

    // Endpoint para registrar un nuevo usuario (opcional)
    @POST("v1/accounts:signUp")
    suspend fun register(
        @Query("key") apiKey: String,
        @Body request: AuthRequest
    ): Response<AuthResponse>

    //
    @POST("v1/accounts:signInWithCustomToken")
    suspend fun signInWithCustomToken(
        @Query("key") apiKey: String,
        @Body request: AuthRequest2
    ): Response<AuthResponse2>

    // Interfaz de la API para el refresco

    @POST("v1/token") // La URL base debe ser https://securetoken.googleapis.com
    suspend fun refreshToken(
        @Query("key") apiKey: String,
        @Body request: TokenRefreshRequest
    ): Response<TokenRefreshResponse>

}