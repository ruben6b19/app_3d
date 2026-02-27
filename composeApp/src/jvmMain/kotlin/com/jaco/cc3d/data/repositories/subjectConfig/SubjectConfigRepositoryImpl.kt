package com.jaco.cc3d.data.repositories.subjectConfig

import com.jaco.cc3d.data.network.subjectConfig.SubjectConfigDto
import com.jaco.cc3d.data.network.subjectConfig.SubjectConfigRequest
import com.jaco.cc3d.data.network.subjectConfig.SubjectConfigService
import com.jaco.cc3d.domain.repositories.subjectConfig.SubjectConfigRepository
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.data.network.utils.bodyOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectConfigRepositoryImpl @Inject constructor(
    private val subjectConfigService: SubjectConfigService
) : SubjectConfigRepository {

    /**
     * Función auxiliar para envolver las llamadas en el helper safeApiCall
     * que ya utilizas en otros repositorios.
     */
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    /**
     * Guarda o actualiza la configuración (Upsert).
     * Envía el public_id de Cloudinary y el mapa de diapositivas al servidor.
     */
    override suspend fun saveSubjectConfig(request: SubjectConfigRequest): Result<SubjectConfigDto> = apiCall {
        subjectConfigService.saveSubjectConfig(request).bodyOrThrow()
    }

    /**
     * Obtiene la configuración actual de una materia.
     * Si la materia no tiene configuración aún, el backend devolverá 404
     * y safeApiCall lo capturará como un Failure.
     */
    override suspend fun getSubjectConfig(subjectId: String): Result<SubjectConfigDto> = apiCall {
        subjectConfigService.getSubjectConfig(subjectId).bodyOrThrow()
    }
}