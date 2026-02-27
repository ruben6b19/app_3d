package com.jaco.cc3d.data.network.course

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
import retrofit2.http.QueryMap

interface CourseService {

    // --- 1. CREATE: Crear un nuevo curso ---
    @POST("courses/")
    suspend fun createCourse(
        @Body request: CourseRequest
    ): Response<BackendResponseWrapper<CourseDto>>


    // --- 2. READ ALL: Obtener todos los cursos (paginado) ---
    // RUTA: GET /courses/all/{page}?limit=...&query=...
    @GET("courses/all/{page}")
    suspend fun getAllCourses(
        @Path("page") page: Int,
        @QueryMap filter: Map<String, String> = emptyMap()
    ): Response<BackendResponseWrapper<PaginationResponse<CourseDto>>>


    // --- 3. READ ONE: Obtener un curso por ID ---
    // RUTA: GET /courses/{courseId}
    @GET("courses/{courseId}")
    suspend fun getCourseById(
        @Path("courseId") courseId: String
    ): Response<BackendResponseWrapper<CourseDto>>


    // --- 4. UPDATE: Actualizar un curso existente ---
    // RUTA: PATCH /courses/{courseId}
    @PATCH("courses/{courseId}")
    suspend fun updateCourse(
        @Path("courseId") courseId: String,
        @Body request: CourseRequest
    ): Response<BackendResponseWrapper<CourseDto>>


    // --- 5. DELETE: Eliminar un curso ---
    // RUTA: DELETE /courses/{courseId}
    @DELETE("courses/{courseId}")
    suspend fun deleteCourse(
        @Path("courseId") courseId: String
    ): Response<BackendResponseWrapper<Unit>> // Usa Unit si solo esperas un 204 o mensaje de éxito
}