package com.jaco.cc3d.domain.usecases.subject

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.Subject
import com.jaco.cc3d.domain.models.SubjectDomainRequest
import com.jaco.cc3d.domain.repositories.subject.SubjectRepository // 💡 Usamos la interfaz del dominio
import javax.inject.Inject

/**
 * Use Case para crear una nueva materia.
 * Recibe el modelo de Dominio (Request) y lo transforma a un DTO (Request) para el Repositorio.
 */
class CreateSubject @Inject constructor(
    private val repository: SubjectRepository // Inyectamos la interfaz
) {
    suspend operator fun invoke(request: SubjectDomainRequest): Result<Subject> {
        // 1. Mapear el modelo de dominio a la solicitud de datos
        val subjectRequestDto = request.toDataRequest()

        // 2. Llamar al repositorio y manejar el resultado
        return repository.createSubject(subjectRequestDto)
            .mapCatching { dto ->
                // 3. Mapear el DTO de respuesta (la nueva materia) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}