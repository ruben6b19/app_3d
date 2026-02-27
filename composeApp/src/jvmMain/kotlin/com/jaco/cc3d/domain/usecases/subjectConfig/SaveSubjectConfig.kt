package com.jaco.cc3d.domain.usecases.subjectConfig


import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.SubjectConfig
import com.jaco.cc3d.domain.models.SubjectConfigDomainRequest
import com.jaco.cc3d.domain.repositories.subjectConfig.SubjectConfigRepository
import javax.inject.Inject

/**
 * Use Case para guardar o actualizar la configuración de una materia (MD y Slides).
 * Implementa la lógica de mapeo entre capas de Dominio y Datos.
 */
class SaveSubjectConfig @Inject constructor(
    private val repository: SubjectConfigRepository
) {
    suspend operator fun invoke(request: SubjectConfigDomainRequest): Result<SubjectConfig> {
        // 1. Mapear el modelo de dominio a la solicitud de datos (Data Layer)
        val dataRequest = request.toDataRequest()

        // 2. Ejecutar la operación en el repositorio y transformar el resultado
        return repository.saveSubjectConfig(dataRequest)
            .mapCatching { dto ->
                // 3. Mapear el DTO de respuesta de vuelta al modelo de Dominio
                dto.toDomainModel()
            }
    }
}