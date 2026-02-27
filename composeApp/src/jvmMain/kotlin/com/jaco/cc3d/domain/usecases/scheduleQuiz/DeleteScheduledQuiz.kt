package com.jaco.cc3d.domain.usecases.scheduleQuiz

import com.jaco.cc3d.domain.repositories.scheduledQuiz.ScheduledQuizRepository
import javax.inject.Inject

class DeleteScheduledQuiz @Inject constructor(
    private val repository: ScheduledQuizRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteScheduledQuiz(id)
    }
}