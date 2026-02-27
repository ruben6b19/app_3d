package com.jaco.cc3d.domain.usecases.subject

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.Subject
import com.jaco.cc3d.domain.models.SubjectDomainRequest
import com.jaco.cc3d.domain.repositories.subject.SubjectRepository // 💡 Usamos la interfaz del dominio
import javax.inject.Inject

/**
 * Use Case para actualizar una materia existente.
 * Recibe el ID de la materia y los nuevos datos del Request de Dominio.
 */
class UpdateSubject @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(subjectId: String, request: SubjectDomainRequest): Result<Subject> {
        // 1. Mapear el Request de Dominio al DTO de solicitud
        val subjectRequestDto = request.toDataRequest()

        // 2. Llamar al repositorio para actualizar
        return repository.updateSubject(subjectId, subjectRequestDto)
            .mapCatching { dto ->
                // 3. Mapear el DTO de respuesta (el instituto actualizado) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}