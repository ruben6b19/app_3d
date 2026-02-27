package com.jaco.cc3d.domain.usecases.auth

import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.domain.repositories.apiAuth.ApiAuthRepository
import javax.inject.Inject

/**
 * Caso de uso encargado de gestionar el refresco de sesión.
 * Centraliza la obtención del token antiguo y la ejecución del refresco.
 */
class RefreshTokenUseCase @Inject constructor(
    private val repository: ApiAuthRepository,
    private val tokenManager: EncryptedDesktopTokenManager
) {
    /**
     * Ejecuta el refresco de token.
     * @return El nuevo Access Token si tuvo éxito, o null si falló.
     */
    suspend operator fun invoke(): String? {
        // 1. Recuperar el refresh token guardado localmente
        val refreshToken = tokenManager.getRefreshToken()

        if (refreshToken.isNullOrBlank()) {
            return null
        }

        // 2. Ejecutar la llamada al repositorio
        // Como el repositorio ya usa 'apiCall', el resultado es un Result<TokenData>
        val result = repository.refreshAccessToken(refreshToken)
        println("refreshToken result: $result")
        // 3. Procesar el resultado
        return result.fold(
            onSuccess = { tokenData ->
                // El repositorio ya guardó los tokens en el disco dentro del apiCall,
                // así que solo devolvemos el nuevo access token al interceptor.
                tokenData.accessToken
            },
            onFailure = { error ->
                // Log opcional del error
                println("RefreshTokenUseCase: Error al refrescar -> ${error.message}")
                null
            }
        )
    }
}