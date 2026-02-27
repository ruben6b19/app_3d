package com.jaco.cc3d.data.repositories.institute

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.institute.InstituteDto
import com.jaco.cc3d.data.network.institute.InstituteRequest
import com.jaco.cc3d.data.network.institute.InstituteService
import com.jaco.cc3d.domain.repositories.institute.InstituteRepository
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.data.network.utils.bodyOrThrow // 💡 Importamos la nueva función
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstituteRepositoryImpl @Inject constructor(
    private val instituteService: InstituteService
) : InstituteRepository {

    // Función auxiliar para manejar la respuesta segura de la API
    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    // --- Versión Limpia con bodyOrThrow() ---
    // bodyOrThrow() ahora maneja el check de isSuccessful, body()?.success == true y lanza HttpException.

    override suspend fun createInstitute(request: InstituteRequest): Result<InstituteDto> = apiCall {
        instituteService.createInstitute(request).bodyOrThrow()
    }

    override suspend fun getAllInstitutes(page: Int, limit: Int, query: String?): Result<PaginationResponse<InstituteDto>> = apiCall {
        instituteService.getAllInstitutes(page, limit, query).bodyOrThrow()
    }

    override suspend fun getInstituteById(instituteId: String): Result<InstituteDto> = apiCall {
        instituteService.getInstituteById(instituteId).bodyOrThrow()
    }

    override suspend fun updateInstitute(instituteId: String, request: InstituteRequest): Result<InstituteDto> = apiCall {
        instituteService.updateInstitute(instituteId, request).bodyOrThrow()
    }

    override suspend fun deleteInstitute(instituteId: String): Result<Unit> = apiCall {
        // bodyOrThrow() verifica el éxito, y luego retornamos Unit para el Result<Unit>
        instituteService.deleteInstitute(instituteId).bodyOrThrow()
        Unit
    }
}