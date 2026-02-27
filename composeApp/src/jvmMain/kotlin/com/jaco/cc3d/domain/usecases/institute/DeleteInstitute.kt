package com.jaco.cc3d.domain.usecases.institute

import com.jaco.cc3d.data.network.common.EmptyResponseData
import com.jaco.cc3d.domain.repositories.institute.InstituteRepository
import javax.inject.Inject
/**
 * Use Case para eliminar un instituto por ID.
 */
class DeleteInstitute @Inject constructor(
    private val repository: InstituteRepository
) {
    // El Use Case solo requiere el ID. Devuelve el resultado del mensaje de confirmación del repositorio.
    suspend operator fun invoke(instituteId: String): Result<Unit> {
        return repository.deleteInstitute(instituteId)
    }
}