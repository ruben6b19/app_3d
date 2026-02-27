package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateDto
import com.jaco.cc3d.data.network.quizTemplate.QuizTemplateRequest
import com.jaco.cc3d.domain.models.QuizTemplate
import com.jaco.cc3d.domain.models.QuizTemplateDomainRequest
import java.util.Date

// =========================================================================
// DEFINICIONES AUXILIARES (ASUMIDAS)
// =========================================================================

// Asumo que los DTOs para referencias pobladas tienen esta forma

// Asumo una función de utilidad para parsear las fechas ISO del backend
fun parseDate(dateString: String): Date {
    // Implementación real necesaria para convertir String a Date
    // Placeholder para efectos de mapeo:
    return Date()
}


// =========================================================================
// MAPPERS DE QUIZTEMPLATE
// =========================================================================

/**
 * Mapea [QuizTemplateDto] (Data Layer) a [QuizTemplate] (Domain Layer).
 * Maneja la conversión de referencias pobladas (Subject, User) y fechas.
 */
fun QuizTemplateDto.toDomainModel(): QuizTemplate {

    return QuizTemplate(
        id = this._id,
        subject = this.subject,
        name = this.name,
        language = this.language,
        createdBy = this.createdBy,
        status = this.status,
        isAlreadyScheduled = this.isAlreadyScheduled ?: false,
        createdAt = parseDate(this.createdAt),
        updatedAt = parseDate(this.updatedAt)
    )
}

/**
 * Mapea [QuizTemplateDomainRequest] (Domain Layer) a [QuizTemplateRequest] (Data Layer).
 * Contiene solo los campos modificables y la ID de referencia (subjectId -> subject).
 */
fun QuizTemplateDomainRequest.toDataRequest(): QuizTemplateRequest {
    return QuizTemplateRequest(
        subject = this.subjectId,
        name = this.name,
        language = this.language,
        status = this.status
    )
}