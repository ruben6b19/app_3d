package com.jaco.cc3d.domain.usecases.quizQuestion

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizQuestion.QuizQuestionRepositoryImpl
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.domain.models.PaginationDomainResponse
import javax.inject.Inject

/**
 * Use Case para obtener preguntas paginadas.
 */
class GetAllQuizQuestions @Inject constructor(
    private val repository: QuizQuestionRepositoryImpl
) {
    /**
     * @param quizTemplateId El ID de la plantilla a la que pertenecen las preguntas.
     */
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 20,
        quizTemplateId: String,
        isRandom: Boolean = false
    ): Result<PaginationDomainResponse<QuizQuestion>> {
        val filtersMap = mutableMapOf<String, String>()
        filtersMap["limit"] = limit.toString()
        filtersMap["quizTemplateId"] = quizTemplateId // Filtro obligatorio para el contexto
        if (isRandom) {
            filtersMap["random"] = "true"
        }

        return repository.getAllQuizQuestions(page, filtersMap)
            .mapCatching { paginationDto ->
                val domainList = paginationDto.docs.map { it.toDomainModel() }

                PaginationDomainResponse(
                    docs = domainList,
                    totalDocs = paginationDto.totalDocs,
                    limit = paginationDto.limit,
                    totalPages = paginationDto.totalPages,
                    page = paginationDto.page,
                    pagingCounter = paginationDto.pagingCounter,
                    hasPrevPage = paginationDto.hasPrevPage,
                    hasNextPage = paginationDto.hasNextPage,
                    prevPage = paginationDto.prevPage,
                    nextPage = paginationDto.nextPage
                )
            }
    }
}