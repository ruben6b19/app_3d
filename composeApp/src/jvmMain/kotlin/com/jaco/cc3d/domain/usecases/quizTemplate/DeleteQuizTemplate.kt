package com.jaco.cc3d.domain.usecases.quizTemplate

import com.jaco.cc3d.data.repositories.quizTemplate.QuizTemplateRepositoryImpl
import javax.inject.Inject

/**
 * Use Case para eliminar una plantilla de quiz por su ID.
 * Delega la eliminación a la capa de Repositorio.
 */
class DeleteQuizTemplate @Inject constructor(
    private val repository: QuizTemplateRepositoryImpl
) {
    /**
     * Invoca el caso de uso para eliminar una plantilla.
     * @param templateId El ID de la plantilla a eliminar.
     * @return Result<Unit> indicando éxito (Unit) o un error.
     */
    suspend operator fun invoke(templateId: String): Result<Unit> {
        return repository.deleteQuizTemplate(templateId)
    }
}