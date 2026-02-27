package com.jaco.cc3d.domain.usecases.quizTemplate

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizTemplate.QuizTemplateRepositoryImpl
import com.jaco.cc3d.domain.models.QuizTemplate
import com.jaco.cc3d.domain.models.QuizTemplateDomainRequest
import javax.inject.Inject

/**
 * Use Case para actualizar una plantilla de quiz existente.
 * Mapea [QuizTemplateDomainRequest] a [QuizTemplateRequest] y el DTO de respuesta a [QuizTemplate].
 */
class UpdateQuizTemplate @Inject constructor(
    private val repository: QuizTemplateRepositoryImpl
) {
    /**
     * Invoca el caso de uso para actualizar una plantilla.
     * @param templateId El ID de la plantilla a actualizar.
     * @param request El modelo de solicitud de dominio con los datos actualizados.
     * @return Result que contiene el modelo de dominio [QuizTemplate] actualizado, o un error.
     */
    suspend operator fun invoke(templateId: String, request: QuizTemplateDomainRequest): Result<QuizTemplate> {
        val quizTemplateRequestDto = request.toDataRequest()

        return repository.updateQuizTemplate(templateId, quizTemplateRequestDto)
            .mapCatching { dto ->
                // Mapear el DTO de respuesta (la plantilla actualizada) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}