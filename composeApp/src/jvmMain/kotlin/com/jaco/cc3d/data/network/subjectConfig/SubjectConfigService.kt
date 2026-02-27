package com.jaco.cc3d.data.network.subjectConfig

import com.jaco.cc3d.data.network.common.BackendResponseWrapper
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de Retrofit para interactuar con la configuración del editor MD (SubjectConfig).
 */
interface SubjectConfigService {

    /**
     * Guarda o actualiza la configuración de una materia.
     * Se usa tanto para la primera subida del MD como para ediciones posteriores.
     */
    @POST("subject-configs")
    suspend fun saveSubjectConfig(
        @Body request: SubjectConfigRequest
    ): Response<BackendResponseWrapper<SubjectConfigDto>>

    /**
     * Obtiene la configuración de una materia específica por su ID.
     * Útil para recuperar el contentUrl (Cloudinary ID) y los IDs de las diapositivas.
     */
    @GET("subject-configs/{subjectId}")
    suspend fun getSubjectConfig(
        @Path("subjectId") subjectId: String
    ): Response<BackendResponseWrapper<SubjectConfigDto>>
}