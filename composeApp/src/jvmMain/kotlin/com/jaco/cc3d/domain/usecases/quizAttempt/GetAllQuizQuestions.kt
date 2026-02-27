package com.jaco.cc3d.domain.usecases.quizAttempt

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizAttempt.StudentQuizAttemptRepositoryImpl
import com.jaco.cc3d.domain.models.StudentQuizAttempt
import com.jaco.cc3d.domain.models.PaginationDomainResponse
import javax.inject.Inject

class GetAllStudentAttempts @Inject constructor(
    private val repository: StudentQuizAttemptRepositoryImpl
) {
    suspend operator fun invoke(
        page: Int = 1,
        studentId: String? = null,
        scheduledQuizId: String? = null
    ): Result<PaginationDomainResponse<StudentQuizAttempt>> {
        val filters = mutableMapOf<String, String>()
        studentId?.let { filters["studentId"] = it }
        scheduledQuizId?.let { filters["scheduledQuizId"] = it }

        return repository.getAllAttempts(page, filters)
            .mapCatching { paginationDto ->
                PaginationDomainResponse(
                    docs = paginationDto.docs.map { it.toDomainModel() },
                    totalDocs = paginationDto.totalDocs,
                    limit = paginationDto.limit,
                    totalPages = paginationDto.totalPages,
                    page = paginationDto.page,
                    pagingCounter = paginationDto.pagingCounter,
                    hasPrevPage = paginationDto.hasPrevPage,
                    hasNextPage = paginationDto.hasNextPage,
                    prevPage = paginationDto.prevPage,
                    nextPage = paginationDto.nextPage
                )
            }
    }
}