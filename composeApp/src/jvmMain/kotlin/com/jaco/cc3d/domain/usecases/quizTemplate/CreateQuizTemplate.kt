package com.jaco.cc3d.domain.usecases.quizTemplate

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizTemplate.QuizTemplateRepositoryImpl
import com.jaco.cc3d.domain.models.QuizTemplate
import com.jaco.cc3d.domain.models.QuizTemplateDomainRequest
import javax.inject.Inject

/**
 * Use Case para crear una nueva plantilla de quiz.
 * Mapea [QuizTemplateDomainRequest] a [QuizTemplateRequest] y el DTO de respuesta a [QuizTemplate].
 */
class CreateQuizTemplate @Inject constructor(
    private val repository: QuizTemplateRepositoryImpl
) {
    /**
     * Invoca el caso de uso para crear una nueva plantilla.
     * @param request El modelo de solicitud de dominio con los datos de la nueva plantilla.
     * @return Result que contiene el modelo de dominio [QuizTemplate] creado, o un error.
     */
    suspend operator fun invoke(request: QuizTemplateDomainRequest): Result<QuizTemplate> {
        val quizTemplateRequestDto = request.toDataRequest()

        return repository.createQuizTemplate(quizTemplateRequestDto)
            .mapCatching { dto ->
                // Mapear el DTO de respuesta (la plantilla recién creada) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}