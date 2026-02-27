package com.jaco.cc3d.data.network.apiAuth

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiAuthService {

    @POST("users/verify-token") // O la ruta completa
    suspend fun verifyToken(
        @Header("No-Auth") noAuthHeader: String = "true",
        @Body request: ExchangeRequest
    ): Response<BackendResponseWrapper<ExchangeResponse>>

    @POST("users/refresh-token")
    suspend fun refreshAccessToken(
        @Header("No-Auth") noAuth: String = "true", // 🎯 Evita el bucle infinito
        @Body request: RefreshTokenRequest
    ): Response<BackendResponseWrapper<TokenData>>
}