package com.jaco.cc3d.domain.repositories.apiAuth

import com.jaco.cc3d.data.network.apiAuth.ExchangeResponse
import com.jaco.cc3d.data.network.apiAuth.TokenData

interface ApiAuthRepository {
    //suspend fun exchangeToken(localId: String): String

    suspend fun verifyToken(idTokenFinal: String, refreshToken: String): Result<ExchangeResponse>

    suspend fun refreshAccessToken(refreshToken: String): Result<TokenData>
}