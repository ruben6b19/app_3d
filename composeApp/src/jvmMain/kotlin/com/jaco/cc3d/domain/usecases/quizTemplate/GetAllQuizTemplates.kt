package com.jaco.cc3d.domain.usecases.quizTemplate

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.quizTemplate.QuizTemplateRepositoryImpl
import com.jaco.cc3d.domain.models.QuizTemplate
import com.jaco.cc3d.domain.models.PaginationDomainResponse
import javax.inject.Inject

/**
 * Use Case para obtener una lista paginada de plantillas de quiz.
 * Mapea la respuesta de paginación de DTOs a una respuesta de dominio con modelos [QuizTemplate].
 */
class GetAllQuizTemplates @Inject constructor(
    private val repository: QuizTemplateRepositoryImpl
) {
    /**
     * Invoca el caso de uso para obtener todas las plantillas.
     * @param page Número de página.
     * @param filter Mapa de filtros (ej. "subjectId", "category", "status").
     * @return Result que contiene la respuesta paginada con modelos [QuizTemplate], o un error.
     */
    suspend operator fun invoke(
        page: Int,
        limit: Int = 10,
        subjectId: String? = null,
        language: String? = null,
        courseId: String? = null
    ): Result<PaginationDomainResponse<QuizTemplate>> {
        val filtersMap = mutableMapOf<String, String>()
        filtersMap["limit"] = limit.toString()

        subjectId?.let { filtersMap["subjectId"] = it }
        language?.let { filtersMap["language"] = it }
        courseId?.let { filtersMap["courseId"] = it }

        return repository.getAllQuizTemplates(page, filtersMap)
            .mapCatching { paginationDto ->
                // Mapear la lista de DTOs a modelos de Dominio
                val domainList = paginationDto.docs.map { it.toDomainModel() }

                // Mapear el objeto de paginación DTO a un objeto de paginación de Dominio
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