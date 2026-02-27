package com.jaco.cc3d.domain.usecases.scheduleQuiz

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.ScheduledQuiz
import com.jaco.cc3d.domain.repositories.scheduledQuiz.ScheduledQuizRepository
import javax.inject.Inject

/**
 * Use Case para obtener la lista de exámenes programados con filtros.
 */
class GetScheduledQuizzesByCourseUseCase @Inject constructor(
    private val repository: ScheduledQuizRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 10,
        courseId: String? = null,
        status: Int? = null
    ): Result<List<ScheduledQuiz>> {

        val filtersMap = mutableMapOf<String, String>()
        filtersMap["limit"] = limit.toString()

        // Filtros opcionales específicos de ScheduledQuiz
        courseId?.let { filtersMap["courseId"] = it }
        status?.let { filtersMap["status"] = it.toString() }

        return repository.getAllScheduledQuizzes(page, filtersMap)
            .mapCatching { paginationResponse ->
                paginationResponse.docs.map { it.toDomainModel() }
            }
    }
}