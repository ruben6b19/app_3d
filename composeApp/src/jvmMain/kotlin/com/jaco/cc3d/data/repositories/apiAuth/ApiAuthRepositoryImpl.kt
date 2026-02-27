package com.jaco.cc3d.data.repositories.apiAuth

import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.network.apiAuth.ApiAuthService
import com.jaco.cc3d.data.network.apiAuth.ExchangeRequest
import com.jaco.cc3d.data.network.apiAuth.ExchangeResponse
import com.jaco.cc3d.data.network.apiAuth.RefreshTokenRequest
import com.jaco.cc3d.data.network.apiAuth.TokenData
import com.jaco.cc3d.domain.repositories.apiAuth.ApiAuthRepository
import com.jaco.cc3d.data.network.utils.safeApiCall
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiAuthRepositoryImpl @Inject constructor(
    private val apiAuthService: ApiAuthService,
    private val tokenManager: EncryptedDesktopTokenManager
): ApiAuthRepository {


    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    // 💡 CAMBIO 1: El tipo de retorno debe ser Result<ExchangeResponse>
    // Se asume que ExchangeResponse es el tipo de dato que el UseCase necesita.
    override suspend fun verifyToken(idTokenFinal: String, refreshToken: String): Result<ExchangeResponse> = apiCall {

        // 1. Ejecutar la llamada a la API
        val response = apiAuthService.verifyToken(request = ExchangeRequest(idTokenFinal, refreshToken))

        // 2. Comprobación de éxito y extracción de datos
        if (response.isSuccessful) {
            // Extraer y devolver el Custom Token (ExchangeResponse)
            response.body()?.data
                ?: throw IllegalStateException("Respuesta exitosa del servidor, pero el Custom Token es nulo.")
        } else {
            // 3. Fallo: Lanzamos HttpException. safeApiCall lo capturará,
            // leerá el errorBody y propagará el mensaje traducido.
            throw HttpException(response)
        }
    }

    override suspend fun refreshAccessToken(refreshToken: String): Result<TokenData> = apiCall {

        // 1. Ejecutar la llamada a la API de refresco
        val response = apiAuthService.refreshAccessToken(request = RefreshTokenRequest(refreshToken))

        // 2. Comprobación de éxito
        if (response.isSuccessful) {
            val data = response.body()?.data
                ?: throw IllegalStateException("Respuesta exitosa del servidor, pero los datos del token son nulos.")

            // 💾 3. Persistencia: Guardamos los nuevos tokens antes de retornar
            // Esto asegura que si el apiCall tiene éxito, el disco ya está actualizado
            tokenManager.saveAccessToken(data.accessToken)
            data.refreshToken?.let {
                tokenManager.saveRefreshToken(it)
            }

            data // Retornamos el objeto TokenData
        } else {
            // 4. Fallo: Lanzamos HttpException para que safeApiCall maneje el error
            throw HttpException(response)
        }
    }

}