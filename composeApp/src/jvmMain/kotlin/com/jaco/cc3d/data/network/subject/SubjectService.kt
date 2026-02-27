package com.jaco.cc3d.data.network.subject

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import com.jaco.cc3d.data.network.common.EmptyResponseData
import com.jaco.cc3d.data.network.common.PaginationResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de Retrofit para interactuar con los endpoints de Materias.
 */
interface SubjectService {

    @POST("subjects")
    suspend fun createSubject(
        @Body request: SubjectRequest
    ): Response<BackendResponseWrapper<SubjectDto>>

    @GET("subjects/all/{page}")
    suspend fun getAllSubjects(
        @Path("page") page: Int,
        @Query("limit") limit: Int,
        @Query("query") query: String?
    ): Response<BackendResponseWrapper<PaginationResponse<SubjectDto>>>

    @GET("subjects/{id}")
    suspend fun getSubjectById(
        @Path("id") subjectId: String
    ): Response<BackendResponseWrapper<SubjectDto>>

    @PATCH("subjects/{id}")
    suspend fun updateSubject(
        @Path("id") subjectId: String,
        @Body request: SubjectRequest
    ): Response<BackendResponseWrapper<SubjectDto>>

    @DELETE("subjects/{id}")
    suspend fun deleteSubject(
        @Path("id") subjectId: String
    ): Response<BackendResponseWrapper<Unit>>
}