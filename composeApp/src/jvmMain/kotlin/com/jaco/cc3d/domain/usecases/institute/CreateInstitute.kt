package com.jaco.cc3d.domain.usecases.institute

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.Institute
import com.jaco.cc3d.domain.models.InstituteDomainRequest
import com.jaco.cc3d.data.repositories.institute.InstituteRepositoryImpl
import javax.inject.Inject


/**
 * Use Case para crear un nuevo instituto.
 * Recibe el modelo de Dominio (Request) y lo transforma a un DTO (Request) para el Repositorio.
 */
class CreateInstitute @Inject constructor(
    private val repository: InstituteRepositoryImpl
) {
    suspend operator fun invoke(request: InstituteDomainRequest): Result<Institute> {
        val instituteRequestDto = request.toDataRequest()

        return repository.createInstitute(instituteRequestDto)
            .mapCatching { dto ->
                // Mapeamos el DTO de respuesta (el nuevo instituto) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}