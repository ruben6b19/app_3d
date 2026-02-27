package com.jaco.cc3d.domain.usecases.subject

import com.jaco.cc3d.domain.repositories.subject.SubjectRepository // 💡 Usamos la interfaz del dominio
import javax.inject.Inject

/**
 * Use Case para eliminar una materia por ID (típicamente cambiando su estado a inactivo).
 */
class DeleteSubject @Inject constructor(
    private val repository: SubjectRepository
) {
    // El Use Case solo requiere el ID. Devuelve el resultado de la operación del repositorio.
    suspend operator fun invoke(subjectId: String): Result<Unit> {
        return repository.deleteSubject(subjectId)
    }
}