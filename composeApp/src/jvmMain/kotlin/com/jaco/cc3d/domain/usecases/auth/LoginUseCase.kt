package com.jaco.cc3d.domain.usecases.auth

import com.jaco.cc3d.data.local.dao.UserDao
import com.jaco.cc3d.data.local.entities.UserEntity
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.repositories.auth.AuthRepositoryImpl
import com.jaco.cc3d.data.repositories.apiAuth.ApiAuthRepositoryImpl
import javax.inject.Inject

class AuthenticationException(message: String, cause: Throwable? = null) : Exception(message, cause)
class VerificationException(message: String, cause: Throwable? = null) : Exception(message, cause)

// ... (tus data classes AuthSessionData y UserData)

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val backendRepository: ApiAuthRepositoryImpl,
    private val tokenManager: EncryptedDesktopTokenManager,
    private val userDao: UserDao
) {
    // 💡 CAMBIO CLAVE: El UseCase ahora devuelve Result<Unit> para indicar éxito o fallo.
    suspend operator fun invoke(email: String, password: String): Result<Unit> {

        // --- 1. Login inicial con Firebase ---
        val sessionData = try {
            authRepository.signInWithPassword(email, password)
        } catch (e: Exception) {
            return Result.failure(AuthenticationException("Credenciales incorrectas o error de red.", e))
        }

        // --- 2. Verificación y Refresh Automático en un solo viaje ---
        // Ahora enviamos ambos tokens al backend
        val verificationResult = backendRepository.verifyToken(
            idTokenFinal = sessionData.idToken,
            refreshToken = sessionData.refreshToken ?: ""
        )

        return verificationResult.fold(
            onSuccess = { response ->
                // ✅ ÉXITO: El backend ya nos dio el token con claims y el tiempo de expiración

                // 3. Guardar en el TokenManager (Memoria/Seguridad Desktop)
                tokenManager.saveAccessToken(response.accessToken)
                tokenManager.saveRefreshToken(response.refreshToken)
                tokenManager.saveUserData(response.user)
                tokenManager.saveExpiresAt(response.expiresAt)

                // 4. PERSISTENCIA EN ROOM (Para Login Offline)
                try {
                    val userEntity = UserEntity(
                        mongoDbId = response.user._id,
                        name = response.user.fullName,
                        email = response.user.email,
                        idToken = response.accessToken,
                        refreshToken = response.refreshToken,
                        expiresAt = response.expiresAt, // 👈 Guardamos el timestamp
                        role = response.user.role.joinToString(","),
                        instituteId = response.user.institute?._id ?: "",
                        language = response.user.institute?.language ?: "es"
                    )
                    userDao.saveSession(userEntity)
                } catch (e: Exception) {
                    println("Error guardando sesión en Room: ${e.message}")
                }

                Result.success(Unit)
            },
            onFailure = { exception ->
                Result.failure(VerificationException(exception.message ?: "Error al validar con el servidor.", exception))
            }
        )
    }
}