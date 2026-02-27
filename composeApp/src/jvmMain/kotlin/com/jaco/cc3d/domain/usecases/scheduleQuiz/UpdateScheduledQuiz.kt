package com.jaco.cc3d.domain.usecases.scheduleQuiz

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.ScheduledQuiz
import com.jaco.cc3d.domain.models.ScheduledQuizDomainRequest
import com.jaco.cc3d.domain.repositories.scheduledQuiz.ScheduledQuizRepository
import javax.inject.Inject

class UpdateScheduledQuiz @Inject constructor(
    private val repository: ScheduledQuizRepository
) {
    suspend operator fun invoke(id: String, request: ScheduledQuizDomainRequest): Result<ScheduledQuiz> {
        val dataRequest = request.toDataRequest()

        return repository.updateScheduledQuiz(id, dataRequest)
            .mapCatching { dto ->
                dto.toDomainModel()
            }
    }
}