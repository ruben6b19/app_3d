package com.jaco.cc3d.data.network.utils

import com.jaco.cc3d.data.network.common.ApiErrorResponse // Tu modelo de error del backend (success: false, message: "...")
import com.jaco.cc3d.domain.SessionExpiredException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

val json = Json { ignoreUnknownKeys = true } // Instancia de Json

// --- NUEVA FUNCIÓN AUXILIAR PARA HTTP EXCEPTION ---
fun extractHttpErrorMessage(
    httpException: HttpException,
    defaultMessage: String
): String {
    val errorBodyString = httpException.response()?.errorBody()?.string()
    val codeMessage = "Error HTTP ${httpException.code()}"
    val httpCode = httpException.code()

    if (errorBodyString != null) {
        return try {
            val apiError = json.decodeFromString(ApiErrorResponse.serializer(), errorBodyString)
            apiError.message
        } catch (e: Exception) {
            httpException.message() ?: "$codeMessage: Error de deserialización en el cuerpo del error."
        }
    } else {
        return when (httpCode) {
            404 -> "Recurso no encontrado (404). El servidor no tiene esta ruta."
            500 -> "Error interno del servidor (500). Por favor, inténtalo de nuevo más tarde."
            else -> "$codeMessage: $defaultMessage"
        }
    }
}

/**
 * Envuelve una llamada suspendida a la API de Retrofit.
 * Detecta 401 para lanzar SessionExpiredException.
 */
suspend inline fun <T> safeApiCall(crossinline call: suspend () -> T): Result<T> {
    return try {
        val data = call()
        Result.success(data)
    } catch (e: HttpException) {
        // 1. Manejo Prioritario de Sesión
        if (e.code() == 401) {
            // Lanzamos la excepción específica de dominio
            return Result.failure(SessionExpiredException("Su sesión ha expirado. Por favor, vuelva a iniciar sesión."))
        }

        // 2. Extraer mensaje del backend para otros errores HTTP (400, 404, 500)
        val errorMessage = try {
            // Importante: No consumas el errorBody aquí si lo vas a usar después,
            // pero como estamos en el catch final, está bien.
            val errorBodyString = e.response()?.errorBody()?.string()
            if (!errorBodyString.isNullOrBlank()) {
                val apiError = json.decodeFromString(ApiErrorResponse.serializer(), errorBodyString)
                apiError.message
            } else {
                e.message() ?: "Error de servidor (${e.code()})"
            }
        } catch (inner: Exception) {
            "Error de comunicación (${e.code()})"
        }

        Result.failure(Exception(errorMessage))

    } catch (e: IOException) {
        // 💡 CAMBIO CRUCIAL:
        // Si hay una excepción dentro de los interceptores (como tu NPE),
        // queremos saber qué pasó realmente, no solo mostrar "Error de red".

        val message = when (e) {
            is SocketTimeoutException -> "Tiempo de espera agotado. Revisa tu conexión."
            is UnknownHostException -> "No se pudo contactar con el servidor."
            else -> e.localizedMessage ?: "Fallo de conexión"
        }

        // Pasamos 'e' como la causa para que el rastro no se pierda
        Result.failure(Exception(message, e))

    } catch (e: Exception) {
        // Si es una excepción de tiempo de ejecución (como el NPE del interceptor)
        Result.failure(e)
    }
}


suspend inline fun <T> safeApiCall2(crossinline call: suspend () -> T): Result<T> {
    return try {
        // 1. Ejecuta la llamada a la API.
        val data = call()
        Result.success(data)
    } catch (e: IOException) {
        // 2. Manejo de errores de red (Ej. sin conexión, timeout).
        val networkErrorMessage = when (e) {
            is SocketTimeoutException -> {
                "Error de red: La conexión expiró (timeout). Por favor, revisa tu conexión a Internet o inténtalo de nuevo."
            }
            is UnknownHostException -> {
                "Error de red: No se pudo encontrar el servidor. Verifica la URL de la API o tu conexión."
            }
            else -> {
                "Error de red: Fallo de conexión o comunicación. Asegúrate de estar en línea."
            }
        }
        Result.failure(Exception(networkErrorMessage))
    } catch (e: HttpException) {
        // 3. Manejo de errores HTTP (Códigos 4xx, 5xx).

        // 💡 AQUÍ ESTÁ LA SOLUCIÓN:
        // Si el código es 401, lanzamos SessionExpiredException inmediatamente.
        if (e.code() == 401) {
            return Result.failure(SessionExpiredException("Sesión expirada (401). Debes iniciar sesión nuevamente."))
        }

        // Para otros errores (400, 404, 500, etc.), extraemos el mensaje del backend.
        val errorMessage = extractHttpErrorMessage(
            httpException = e,
            defaultMessage = "Fallo en la comunicación con el servidor."
        )

        Result.failure(Exception(errorMessage))

    } catch (e: Exception) {
        // 4. Cualquier otra excepción inesperada.
        Result.failure(Exception("Error inesperado: Fallo interno de la aplicación. (${e::class.simpleName})"))
    }
}