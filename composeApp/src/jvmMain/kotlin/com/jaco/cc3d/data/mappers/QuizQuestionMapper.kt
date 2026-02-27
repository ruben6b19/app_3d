package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.network.quizQuestion.OptionDto
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionDto
import com.jaco.cc3d.data.network.quizQuestion.QuizQuestionRequest
import com.jaco.cc3d.domain.models.QuizOption
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.domain.models.QuizQuestionDomainRequest

// =========================================================================
// MAPPERS DE QUIZQUESTION (Simple - Referencias como String)
// =========================================================================

/**
 * Mapea [OptionDto] (Data) a [QuizOption] (Domain).
 */
fun OptionDto.toDomainModel(): QuizOption = QuizOption(
    text = this.text,
    isCorrect = this.isCorrect
)

/**
 * Mapea [QuizOption] (Domain) a [OptionDto] (Data).
 */
fun QuizOption.toDataRequest(): OptionDto = OptionDto(
    text = this.text,
    isCorrect = this.isCorrect
)

/**
 * Mapea [QuizQuestionDto] (Data Layer) a [QuizQuestion] (Domain Layer).
 */
fun QuizQuestionDto.toDomainModel(): QuizQuestion {
    return QuizQuestion(
        id = this._id,
        quizTemplateId = this.quizTemplate,
        questionText = this.questionText,
        questionType = this.questionType,
        // Mapeamos la lista de opciones de DTO a Dominio
        options = this.options?.map { it.toDomainModel() } ?: emptyList(),
        createdBy = this.createdBy,
        status = this.status,
        createdAt = parseDate(this.createdAt),
        updatedAt = parseDate(this.updatedAt)
    )
}

/**
 * Mapea [QuizQuestionDomainRequest] (Domain Layer) a [QuizQuestionRequest] (Data Layer).
 */
fun QuizQuestionDomainRequest.toDataRequest(): QuizQuestionRequest {
    return QuizQuestionRequest(
        quizTemplate = this.quizTemplateId,
        questionText = this.questionText,
        questionType = this.questionType,
        // Mapeamos la lista de opciones de Dominio a DTO de solicitud
        options = this.options.map { it.toDataRequest() },
        status = this.status
    )
}