package com.jaco.cc3d.domain.usecases.course

import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.data.repositories.course.CourseRepositoryImpl
import com.jaco.cc3d.data.mappers.toDomainModel
import javax.inject.Inject

/**
 * Use Case para obtener la lista completa de cursos.
 * Se encarga de llamar al Repositorio y mapear los DTOs a modelos de Dominio.
 */
class GetCourses @Inject constructor(
    private val repository: CourseRepositoryImpl
) {
    /**
     * Invoca el caso de uso para obtener cursos con paginación y filtro.
     * @param page El número de página (por defecto 1).
     * @param limit El límite de elementos por página (por defecto 10).
     * @param query Un string opcional para filtrar los cursos.
     * @return Result<List<Course>> La lista de cursos o un error.
     */
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 10,
        instituteId: String
    ): Result<List<Course>> {

        val filtersMap = mutableMapOf<String, String>()

        // 1. Parámetro de Paginación (limit)
        filtersMap["limit"] = limit.toString()

        // 2. Parámetro de Dominio (instituteId)
        filtersMap["instituteId"] = instituteId

        // 3. Otros filtros (si los hay)
        //filtersMap.putAll(filters)
        // 4. Concatenar los parámetros con '&' para formar la query string final.
        //val finalQuery = params.joinToString("&")
        return repository.getAllCourses(page, filtersMap)
            .mapCatching { paginationResponse ->
                // Mapeamos la lista de DTOs a la lista de modelos de Dominio
                paginationResponse.docs.map { it.toDomainModel() }
            }
    }
}