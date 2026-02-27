package com.jaco.cc3d.domain.repositories.subject

import com.jaco.cc3d.data.network.common.PaginationResponse
import com.jaco.cc3d.data.network.subject.SubjectDto
import com.jaco.cc3d.data.network.subject.SubjectRequest

/**
 * Contrato de la capa Domain para la gestión de Materias.
 * Define las operaciones CRUD que la capa Data debe implementar.
 *
 * NOTA: Esta interfaz usa DTOs de la capa Data, ya que es el límite entre
 * Domain y Data, y el contrato debe reflejar los tipos de datos que la
 * implementación consumirá y producirá desde la red.
 */
interface SubjectRepository {

    /**
     * Crea una nueva materia en el backend.
     * @param request Datos de la materia a crear (nombre, descripción).
     * @return Result que contiene el DTO de la materia creada si es exitoso.
     */
    suspend fun createSubject(request: SubjectRequest): Result<SubjectDto>

    /**
     * Obtiene una lista paginada de materias del backend.
     * @param page Número de página a solicitar (base 1).
     * @param limit Cantidad de elementos por página.
     * @param query Cadena de búsqueda opcional para filtrar.
     * @return Result que contiene la respuesta paginada con la lista de SubjectDto.
     */
    suspend fun getAllSubjects(
        page: Int,
        limit: Int = 10,
        query: String? = null
    ): Result<PaginationResponse<SubjectDto>>

    /**
     * Obtiene una materia específica por su ID.
     * @param subjectId ID de la materia.
     * @return Result que contiene el DTO de la materia.
     */
    suspend fun getSubjectById(subjectId: String): Result<SubjectDto>

    /**
     * Actualiza una materia existente.
     * @param subjectId ID de la materia a actualizar.
     * @param request Datos actualizados (nombre, descripción, estado).
     * @return Result que contiene el DTO de la materia actualizada.
     */
    suspend fun updateSubject(subjectId: String, request: SubjectRequest): Result<SubjectDto>

    /**
     * Elimina una materia (típicamente cambiando su estado a inactivo).
     * @param subjectId ID de la materia a eliminar.
     * @return Result<Unit> indicando el éxito de la operación.
     */
    suspend fun deleteSubject(subjectId: String): Result<Unit>
}