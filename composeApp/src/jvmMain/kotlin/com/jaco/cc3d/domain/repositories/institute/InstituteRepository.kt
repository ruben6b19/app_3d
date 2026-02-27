package com.jaco.cc3d.domain.repositories.institute

import com.jaco.cc3d.data.network.common.EmptyResponseData
import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.institute.InstituteDto
import com.jaco.cc3d.data.network.institute.InstituteRequest

interface InstituteRepository {

    suspend fun createInstitute(request: InstituteRequest): Result<InstituteDto>

    suspend fun getAllInstitutes(page: Int, limit: Int = 10, query: String? = null): Result<PaginationResponse<InstituteDto>>

    suspend fun getInstituteById(instituteId: String): Result<InstituteDto>

    suspend fun updateInstitute(instituteId: String, request: InstituteRequest): Result<InstituteDto>

    suspend fun deleteInstitute(instituteId: String): Result<Unit>
}