package com.jaco.cc3d.data.network.interceptor


import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.network.apiAuth.ApiAuthService
import com.jaco.cc3d.data.network.apiAuth.RefreshTokenRequest
import com.jaco.cc3d.di.Injector
import com.jaco.cc3d.domain.SessionExpiredException
import com.jaco.cc3d.domain.usecases.auth.RefreshTokenUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.runBlocking // Necesario para llamar suspending functions
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    //private val getNewTokenProvider: Lazy<GetNewToken>, // Inyección diferida para evitar ciclos
    private val tokenManager: EncryptedDesktopTokenManager,
    private val refreshTokenUseCaseProvider: dagger.Lazy<RefreshTokenUseCase>
) : Interceptor {

    //private val getNewTokenUseCase: GetNewToken
    //    get() = getNewTokenProvider.get()

    // Mutex para asegurar que solo un hilo intente refrescar el token a la vez
    private val mutex = Mutex()


    companion object {
        private const val NO_AUTH_HEADER = "No-Auth"
        private const val AUTHORIZATION_HEADER = "Authorization"
    }

    // Helper para añadir el token de autorización
    private fun Request.addAuthHeader(token: String): Request {
        // Usamos el formato Bearer, que es estándar para tokens Firebase/JWT
        return this.newBuilder()
            .header(AUTHORIZATION_HEADER, "Bearer $token")
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 1. Manejo de exclusión de token (No-Auth)
        if (originalRequest.header(NO_AUTH_HEADER) != null) {
            return chain.proceed(
                originalRequest.newBuilder().removeHeader(NO_AUTH_HEADER).build()
            )
        }

        val initialToken = tokenManager.getAccessToken()

        // 2. Si no hay token inicial, procede (la API devolverá 401 si lo requiere)
        if (initialToken.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // 3. Añadir token e intentar la llamada
        var response = chain.proceed(originalRequest.addAuthHeader(initialToken))

        // 4. Chequeo de 401: Intento de Refresco
        if (response.code == 401) {
            response.close() // Cierra la respuesta original para liberarla

            // Usa Mutex para sincronizar el refresco
            return runBlocking {
                mutex.withLock {
                    val currentToken = tokenManager.getAccessToken()

                    // a) Verificar si el token ya fue refrescado por otro hilo
                    if (currentToken != initialToken) {
                        // El token ya se refrescó. Reintentar la solicitud con el nuevo token.
                        val newRequest = originalRequest.addAuthHeader(currentToken!!)
                        return@withLock chain.proceed(newRequest)
                    }

                    // b) Intentar refrescar el token
                    //val newAccessToken =null// attemptTokenRefresh()
                    val newAccessToken = attemptTokenRefresh()

                    if (newAccessToken != null) {
                        // Refresco exitoso. Reintentar la solicitud con el token nuevo.
                        val newRequest = originalRequest.addAuthHeader(newAccessToken)
                        chain.proceed(newRequest)
                    } else {
                        // Refresco fallido o no hay refresh token.
                        // Limpiar tokens y dejar que la UI maneje el error de deslogueo.
                        //tokenManager.clearTokens()
                        tokenManager.logout {
                            // 🧹 Aquí adentro escribes TODO lo que quieres borrar de la PC
                            Injector.appDatabase.enrollmentDao().clearAll()
                            Injector.appDatabase.courseDao().clearAll()
                            Injector.appDatabase.userDao().clearAll()
                        }

                        // Lanzar una excepción para forzar el fallo de la llamada original
                        // y notificar al manejador de errores de la red.
                        //throw IOException("Token refresh failed. User must log in again.")
                        throw SessionExpiredException("Token refresh failed. User must log in again.")
                    }
                }
            }
        }

        // 5. Devolver la respuesta si no es 401
        return response
    }

    private suspend fun attemptTokenRefresh(): String? {
        // 🎯 Invocación limpia del Use Case
        return refreshTokenUseCaseProvider.get().invoke()
    }
}