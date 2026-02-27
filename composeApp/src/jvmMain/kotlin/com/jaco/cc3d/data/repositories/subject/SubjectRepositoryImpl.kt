package com.jaco.cc3d.data.repositories.subject

import com.jaco.cc3d.data.network.subject.SubjectService
import com.jaco.cc3d.domain.repositories.subject.SubjectRepository
import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.subject.SubjectDto
import com.jaco.cc3d.data.network.subject.SubjectRequest
import com.jaco.cc3d.data.network.utils.safeApiCall
import com.jaco.cc3d.data.network.utils.bodyOrThrow // 💡 Importamos la nueva función
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepositoryImpl @Inject constructor(
    private val subjectService: SubjectService
) : SubjectRepository {

    private suspend inline fun <T> apiCall(crossinline call: suspend () -> T): Result<T> {
        return safeApiCall(call = call)
    }

    // --- Versión Limpia con bodyOrThrow() ---

    override suspend fun createSubject(request: SubjectRequest): Result<SubjectDto> = apiCall {
        subjectService.createSubject(request).bodyOrThrow()
    }

    override suspend fun getAllSubjects(
        page: Int,
        limit: Int,
        query: String?
    ): Result<PaginationResponse<SubjectDto>> = apiCall {
        subjectService.getAllSubjects(page, limit, query).bodyOrThrow()
    }

    override suspend fun getSubjectById(subjectId: String): Result<SubjectDto> = apiCall {
        subjectService.getSubjectById(subjectId).bodyOrThrow()
    }

    override suspend fun updateSubject(subjectId: String, request: SubjectRequest): Result<SubjectDto> = apiCall {
        subjectService.updateSubject(subjectId, request).bodyOrThrow()
    }

    override suspend fun deleteSubject(subjectId: String): Result<Unit> = apiCall {
        // Ejecutamos la llamada. bodyOrThrow devolverá EmptyResponseData, pero retornamos Unit.
        subjectService.deleteSubject(subjectId).bodyOrThrow()
        Unit
    }
}