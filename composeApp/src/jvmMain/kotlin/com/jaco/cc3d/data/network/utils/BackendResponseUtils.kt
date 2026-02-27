package com.jaco.cc3d.data.network.utils

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import retrofit2.Response
import retrofit2.HttpException

/**
 * Función de extensión que se aplica sobre la respuesta genérica de Retrofit.
 * * Se encarga de:
 * 1. Verificar el éxito HTTP (código 2xx).
 * 2. Verificar el éxito de la API (success == true en el wrapper).
 * 3. Extraer y devolver los datos limpios (T).
 * 4. Lanzar una excepción (HttpException) si alguna de las verificaciones falla.
 * * @throws HttpException Si la llamada HTTP falla o si el wrapper indica 'success: false'.
 * @return El objeto de datos puro (T).
 */
fun <T> Response<BackendResponseWrapper<T>>.bodyOrThrow(): T {

    // 1. Verificar el éxito HTTP y el éxito del wrapper
    if (this.isSuccessful && this.body()?.success == true) {

        // 2. Extraer el dato real. Usamos '!!' aquí porque si success es true,
        // esperamos que data no sea nulo. Si lo es, lanzamos un error interno.
        return this.body()!!.data
            ?: throw IllegalStateException("Successful API response, but 'data' field is missing or null.")

    } else {

        // 3. Si falla, lanzamos HttpException.
        // Tu utilidad safeApiCall capturará esta excepción, leerá el mensaje de error
        // del cuerpo (errorBody) y lo mapeará a Result.failure().
        throw HttpException(this)
    }
}