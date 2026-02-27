package com.jaco.cc3d.data.network.user

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import com.jaco.cc3d.data.network.common.PaginationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    // --- 1. CREATE: Crear un nuevo usuario ---
    // RUTA: POST /users/
    @POST("users/")
    suspend fun createUser(
        @Body request: UserRequest
    ): Response<BackendResponseWrapper<UserDto>>


    // --- 2. READ ALL: Obtener todos los usuarios de un instituto (paginado) ---
    // RUTA: GET /users/institute/{instituteId}/all/{page}?limit=...&query=...
    @GET("users/institute/{instituteId}/all/{page}")
    suspend fun getAllUsersByInstitute(
        @Path("instituteId") instituteId: String, // Nuevo Path para el filtrado
        @Path("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("query") query: String? = null
    ): Response<BackendResponseWrapper<PaginationResponse<UserDto>>>


    // --- 3. READ ONE: Obtener un usuario por ID ---
    // RUTA: GET /users/{userId}
    @GET("users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: String
    ): Response<BackendResponseWrapper<UserDto>>


    // --- 4. UPDATE: Actualizar un usuario existente ---
    // RUTA: PATCH /users/{userId}
    @PATCH("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body request: UserRequest // Nota: El cuerpo puede ser parcial (si la API lo permite)
    ): Response<BackendResponseWrapper<UserDto>>


    // --- 5. DELETE: Eliminar un usuario ---
    // RUTA: DELETE /users/{userId}
    @DELETE("users/{userId}")
    suspend fun deleteUser(
        @Path("userId") userId: String
    ): Response<BackendResponseWrapper<Unit>> // Respuesta simple de confirmación
}