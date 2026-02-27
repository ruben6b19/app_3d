package com.jaco.cc3d.data.network.institute

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import com.jaco.cc3d.data.network.common.EmptyResponseData
import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.institute.InstituteDto
import com.jaco.cc3d.data.network.institute.InstituteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface InstituteService {

    @POST("institutes/")
    suspend fun createInstitute(
        @Body request: InstituteRequest
    ): Response<BackendResponseWrapper<InstituteDto>>


    // --- 2. READ ALL: Obtener todos los institutos (paginado) ---
    // RUTA: GET /institutes/all/{page}?limit=...&query=...
    @GET("institutes/all/{page}")
    suspend fun getAllInstitutes(
        @Path("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("query") query: String? = null
    ): Response<BackendResponseWrapper<PaginationResponse<InstituteDto>>>


    // --- 3. READ ONE: Obtener un instituto por ID ---
    // RUTA: GET /institutes/{instituteId}
    @GET("institutes/{instituteId}")
    suspend fun getInstituteById(
        @Path("instituteId") instituteId: String
    ): Response<BackendResponseWrapper<InstituteDto>>


    // --- 4. UPDATE: Actualizar un instituto existente (Parcial o Total) ---
    // RUTA: PATCH /institutes/{instituteId}
    @PATCH("institutes/{instituteId}")
    suspend fun updateInstitute(
        @Path("instituteId") instituteId: String,
        @Body request: InstituteRequest
    ): Response<BackendResponseWrapper<InstituteDto>>


    // --- 5. DELETE: Eliminar un instituto ---
    // RUTA: DELETE /institutes/{instituteId}
    @DELETE("institutes/{instituteId}")
    suspend fun deleteInstitute(
        @Path("instituteId") instituteId: String
    ): Response<BackendResponseWrapper<Unit>> // Respuesta simple de confirmación (ej. "Deleted successfully")

}