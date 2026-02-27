package com.jaco.cc3d.data.repositories.auth

import com.jaco.cc3d.data.network.auth.AuthService
import com.jaco.cc3d.data.network.auth.AuthRequest
import com.jaco.cc3d.data.network.auth.AuthRequest2
import com.jaco.cc3d.data.network.auth.TokenRefreshRequest
import com.jaco.cc3d.data.network.auth.AuthResponse
import com.jaco.cc3d.data.network.auth.TokenRefreshResponse
import com.jaco.cc3d.domain.repositories.auth.AuthRepository
import com.jaco.cc3d.domain.usecases.auth.AuthenticationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    // Recibimos la API Key (la definiremos en el módulo)
    private val apiKey: String
) : AuthRepository {

    override suspend fun signInWithPassword(email: String, password: String): AuthResponse {
        val requestBody = AuthRequest(
            email = email,
            password = password,
            returnSecureToken = true
        )

        try {
            // 1. Llama al servicio.
            val response = authService.signInWithPassword(apiKey, requestBody)

            // 2. Verifica si la llamada fue exitosa.
            if (response.isSuccessful) {
                val authResponse = response.body()

                // 3. Devuelve el localId del cuerpo de la respuesta.
                return authResponse
                    ?: throw IllegalStateException("Respuesta de autenticación exitosa, pero localId es nulo.")
            } else {
                // Manejo de errores de la API (ej: credenciales inválidas 400)
                val errorMessage = response.errorBody()?.string()
                println("Error de API durante el login: ${response.code()} - $errorMessage")
                // Deberías lanzar una excepción personalizada aquí, ej: CredencialesIncorrectas
                throw Exception("Fallo de autenticación: ${response.code()}")
            }

        } catch (e: Exception) {
            // Manejo de errores de red (Timeout, conexión)
            println("Error durante el login: ${e.message}")
            throw e // Re-lanza la excepción
        }
    }

    override suspend fun signInWithCustomToken(token: String): String { // ⬅️ CAMBIO 1: Retorna String
        // NOTA: Asegúrate de que AuthRequest2 incluye returnSecureToken = true
        val requestBody = AuthRequest2(
            token = token,
            returnSecureToken = true // Es crucial para recibir el ID Token
        )

        try {
            // Utilizamos el servicio inyectado para la llamada a la red
            val response = authService.signInWithCustomToken(apiKey, requestBody)

            if (response.isSuccessful) {
                val authResponse = response.body()

                // ⬅️ CAMBIO 2: Devolver el idToken
                return authResponse?.idToken
                    ?: throw IllegalStateException("Respuesta exitosa de signInWithCustomToken, pero ID Token es nulo.")

            } else {
                // Fallo (código 4xx, 5xx): Llamamos a la función auxiliar para loggear y lanzar la excepción.
                // Es buena práctica lanzar una excepción más específica aquí.
                handleAuthFailure(response, "signInWithCustomToken")
                throw Exception("Fallo en la autenticación con Custom Token.") // Fallback
            }

        } catch (e: Exception) {
            // Manejo de errores de red o API
            println("Error durante el inicio de sesión con Custom Token: ${e.message}")
            throw e // Re-lanza la excepción para ser manejada en el Use Case
        }
    }

    override suspend fun forceTokenRefresh(refreshToken: String): TokenRefreshResponse { // ⬅️ CAMBIO: Devolvemos el modelo de respuesta completo
        // 1. Obtener el Refresh Token guardado
        //val refreshToken = tokenManager.getRefreshToken()
        //    ?: throw IllegalStateException("No se encontró el Refresh Token.")

        // 2. Preparar la solicitud
        val requestBody = TokenRefreshRequest(grantType = "refresh_token", refreshToken = refreshToken)

        // 3. Llamar a la API REST de Firebase
        val response = authService.refreshToken(
            apiKey = apiKey,
            request = requestBody
        )

        if (response.isSuccessful) {
            val newSessionData = response.body() ?: throw IllegalStateException("Respuesta de refresco vacía.")

            // ⚠️ IMPORTANTE: Ya NO guardamos el token aquí. Lo hace el Use Case.

            // 4. Devolver la respuesta completa
            return newSessionData
        } else {
            // Manejo de error de la API de Refresco
            println("Error al refrescar el token: ${response.code()} - ${response.errorBody()?.string()}")
            throw AuthenticationException("Fallo al refrescar la sesión. Vuelva a iniciar sesión.")
        }
    }

    override suspend fun registerUser(email: String, password: String) {
        val requestBody = AuthRequest(
            email = email,
            password = password,
            returnSecureToken = false
        )

        try {
            // Utilizamos el servicio inyectado para la llamada a la red
            val response = authService.register(apiKey, requestBody)
            if (response.isSuccessful) {
                // Éxito (código 2xx): Accedemos al token desde el cuerpo.
                val idToken = response.body()?.idToken
                println("Registro exitoso. ID Token: $idToken")
                // Aquí deberías guardar el token en un DataStore o SharedPreferences
            } else {
                // Fallo (código 4xx, 5xx): Llamamos a la función auxiliar para loggear y lanzar la excepción.
                handleAuthFailure(response, "registro")
            }
            // TODO: Procesar AuthResponse (guardar token, userId, etc.)
            //println("Login exitoso. ID Token: ${response}")

        } catch (e: Exception) {
            // TODO: Manejo de errores de red o API
            println("Error durante el login: ${e.message}")
            throw e // Re-lanza la excepción para ser manejada en la capa de UI/Domain
        }
    }

    /**
     * Maneja un fallo de respuesta de Retrofit (código 4xx/5xx).
     * Intenta extraer el JSON de error de Firebase y lanza una excepción.
     */
    private fun handleAuthFailure(response: Response<*>, operation: String) {
        // 1. Intentamos obtener el cuerpo de error
        val errorBodyString = response.errorBody()?.string()

        // 2. Definimos un mensaje base
        val baseMessage = "Fallo de $operation: Código HTTP ${response.code()}"

        if (errorBodyString.isNullOrBlank()) {
            // Si no hay cuerpo de error (ej: fallo de servidor 500 sin JSON)
            println("ERROR en $operation: $baseMessage. Cuerpo de error vacío.")
            throw RuntimeException(baseMessage)
        }

        // 3. Intentamos parsear el JSON de error de Firebase
        try {
            // El formato de error de Firebase Identity Toolkit es:
            // {"error": {"code": 400, "message": "...", "errors": [...]}}

            // Usamos la misma instancia de Json de tu módulo para asegurar compatibilidad
            val json = Json { ignoreUnknownKeys = true }

            // Creamos un mapa genérico para acceder a la jerarquía de error
            val jsonObject = json.parseToJsonElement(errorBodyString).jsonObject

            // Intentamos extraer el mensaje de error de Firebase
            val errorMessage = jsonObject["error"]?.jsonObject?.get("message")?.jsonPrimitive?.content

            if (errorMessage != null) {
                val finalMessage = "$baseMessage. Mensaje de Firebase: $errorMessage"
                println("ERROR en $operation: $finalMessage")
                // Lanzamos una excepción con el mensaje de error claro de la API
                throw RuntimeException(finalMessage)
            } else {
                // Si el JSON no tiene el campo 'message' donde se esperaba
                println("ERROR en $operation: $baseMessage. Error JSON no parseable: $errorBodyString")
                throw RuntimeException("$baseMessage. Error de API desconocido.")
            }

        } catch (e: Exception) {
            // Si falla el parseo del JSON
            println("ERROR en $operation: $baseMessage. Excepción al parsear error: ${e.message}")
            throw RuntimeException("$baseMessage. Error: ${e.message}")
        }
    }


}