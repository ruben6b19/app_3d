package com.jaco.cc3d.domain.usecases.scheduleQuiz

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.ScheduledQuiz
import com.jaco.cc3d.domain.models.ScheduledQuizDomainRequest
import com.jaco.cc3d.domain.repositories.scheduledQuiz.ScheduledQuizRepository
import javax.inject.Inject

/**
 * Use Case para programar un nuevo examen.
 */
class ScheduleQuiz @Inject constructor(
    private val repository: ScheduledQuizRepository
) {
    suspend operator fun invoke(request: ScheduledQuizDomainRequest): Result<ScheduledQuiz> {
        val dataRequest = request.toDataRequest()

        return repository.createScheduledQuiz(dataRequest)
            .mapCatching { dto ->
                dto.toDomainModel()
            }
    }
}