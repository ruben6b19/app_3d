package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.network.scheduledQuiz.ScheduledQuizDto
import com.jaco.cc3d.data.network.scheduledQuiz.ScheduledQuizRequest
import com.jaco.cc3d.domain.models.ScheduledQuiz
import com.jaco.cc3d.domain.models.ScheduledQuizDomainRequest
import com.jaco.cc3d.domain.models.UserAttemptInfo

// =========================================================================
// MAPPERS DE SCHEDULED QUIZ (EXAMEN PROGRAMADO)
// =========================================================================

/**
 * Mapea [ScheduledQuizDto] (Data Layer) a [ScheduledQuiz] (Domain Layer).
 */
fun ScheduledQuizDto.toDomainModel(): ScheduledQuiz {
    return ScheduledQuiz(
        id = this._id,
        courseId = this.course,
        quizTemplateId = this.quizTemplate,
        quizTitle = this.quizTemplateData?.name ?: "Examen Programado",
        quizDate = this.quizDate,
        details = this.details,
        status = this.status,
        userAttempt = this.userAttempt?.let {
            UserAttemptInfo(
                hasAttempted = it.hasAttempted,
                status = it.status,
                score = it.score
            )
        },
        createdBy = this.createdBy,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Mapea [ScheduledQuizDomainRequest] (Domain Layer) a [ScheduledQuizRequest] (Data Layer).
 * Transforma el modelo de dominio en la solicitud que el Backend espera recibir.
 */
fun ScheduledQuizDomainRequest.toDataRequest(): ScheduledQuizRequest {
    return ScheduledQuizRequest(
        course = this.courseId,
        quizTemplate = this.quizTemplateId,
        quizDate = this.quizDate,
        details = this.details,
        status = this.status
    )
}