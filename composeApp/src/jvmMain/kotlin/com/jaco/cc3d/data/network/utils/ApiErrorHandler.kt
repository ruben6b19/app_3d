package com.jaco.cc3d.data.network.utils

/**
 * Función de utilidad para manejar la lógica de errores comunes de API,
 * especialmente la expiración de sesión.
 *
 * @param exception La excepción lanzada (ej. del Repositorio).
 * @param sessionExpiredMessage El mensaje traducido para sesión expirada.
 * @param unknownErrorMessage El mensaje traducido para errores desconocidos.
 * @param onSessionExpired La lambda que ejecuta la acción específica del ViewModel (ej. mustLogout = true).
 * @return El mensaje de error localizado apropiado.
 */
fun handleApiFailure(
    exception: Throwable,
    sessionExpiredMessage: String,
    unknownErrorMessage: String,
    onSessionExpired: () -> Unit
): String {
    val errorString = exception.toString()
    val errorMessage = exception.message ?: ""
    println("pasa failure "+errorString)
    // 1. Definimos la "huella digital" del error de sesión
    // Buscamos la excepción o el mensaje de error que indica un fallo de token.
    val isSessionError = errorString.contains("SessionExpiredException", ignoreCase = true) ||
            errorString.contains("Token refresh failed", ignoreCase = true) ||
            errorMessage.contains("Token refresh failed", ignoreCase = true)

    if (isSessionError) {
        println("isSessionError "+isSessionError)
        onSessionExpired() // Ejecutar la acción del ViewModel (mustLogout = true)
        return sessionExpiredMessage
    }
    println("pasa failure2 "+errorString)
    // Retorno genérico
    return exception.message ?: unknownErrorMessage
}