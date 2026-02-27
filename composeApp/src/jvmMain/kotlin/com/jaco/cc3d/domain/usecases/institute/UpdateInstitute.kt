package com.jaco.cc3d.domain.usecases.institute

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.Institute
import com.jaco.cc3d.domain.models.InstituteDomainRequest
import com.jaco.cc3d.data.repositories.institute.InstituteRepositoryImpl
import javax.inject.Inject

/**
 * Use Case para actualizar un instituto existente.
 * Requiere que el InstituteDomainRequest contenga el 'id' (aunque tu modelo DomainRequest
 * no lo tiene, es común que la lógica de actualización requiera el ID para saber qué actualizar.
 * Lo manejamos asumiendo que el ID se pasa al constructor del Use Case o a la función invoke.
 */
class UpdateInstitute @Inject constructor(
    private val repository: InstituteRepositoryImpl
) {
    // Se requiere el ID del instituto a actualizar
    suspend operator fun invoke(instituteId: String, request: InstituteDomainRequest): Result<Institute> {
        val instituteRequestDto = request.toDataRequest()

        return repository.updateInstitute(instituteId, instituteRequestDto)
            .mapCatching { dto ->
                // Mapeamos el DTO de respuesta (el instituto actualizado) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}