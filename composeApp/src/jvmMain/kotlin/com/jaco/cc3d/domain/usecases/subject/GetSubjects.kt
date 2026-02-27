package com.jaco.cc3d.domain.usecases.subject

import com.jaco.cc3d.domain.models.Subject
import com.jaco.cc3d.domain.repositories.subject.SubjectRepository // 💡 Usamos la interfaz del dominio
import com.jaco.cc3d.data.mappers.toDomainModel
import javax.inject.Inject

/**
 * Use Case para obtener la lista paginada de materias.
 * Se encarga de llamar al Repositorio y mapear los DTOs a modelos de Dominio.
 */
class GetSubjects @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 10,
        query: String? = null
    ): Result<List<Subject>> {

        return repository.getAllSubjects(page, limit, query)
            .mapCatching { paginationResponse ->
                // Mapeamos la lista de DTOs (docs) a la lista de modelos de Dominio
                paginationResponse.docs.map { it.toDomainModel() }
            }
    }
}