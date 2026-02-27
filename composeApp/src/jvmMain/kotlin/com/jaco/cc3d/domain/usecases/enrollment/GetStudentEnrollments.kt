package com.jaco.cc3d.domain.usecases.enrollment

import com.jaco.cc3d.data.local.dao.EnrollmentDao
import com.jaco.cc3d.data.mappers.toDomain
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.mappers.toEntity
import com.jaco.cc3d.data.repositories.enrollment.EnrollmentRepositoryImpl
import com.jaco.cc3d.domain.models.Enrollment
import javax.inject.Inject

/**
 * Use Case para obtener las inscripciones (cursos/materias) de un estudiante específico.
 */
class GetStudentEnrollments2 @Inject constructor(
    private val repository: EnrollmentRepositoryImpl
) {
    /**
     * Invoca el caso de uso para obtener las materias de un alumno.
     * @param studentId El ID del estudiante (obtenido usualmente del Token o UserData).
     * @param page Página actual.
     * @param limit Cantidad de materias por página.
     * @return Result<List<Enrollment>> Lista de materias en las que está enrolado.
     */
    suspend operator fun invoke(
        studentId: String,
        page: Int = 1,
        limit: Int = 10,
    ): Result<List<Enrollment>> {
        val filtersMap = mutableMapOf<String, String>()

        // Seteamos el filtro 'studentId' que espera tu controlador de Express
        filtersMap["studentId"] = studentId
        filtersMap["limit"] = limit.toString()
        // Opcional: Podrías filtrar solo las activas
        filtersMap["status"] = "1"

        return repository.getAllEnrollments(page, filtersMap)
            .mapCatching { paginationResponse ->
                // Mapeo de DTO (backend) a Domain Model
                paginationResponse.docs.map { it.toDomainModel() }
            }
    }
}
class GetStudentEnrollments @Inject constructor(
    private val repository: EnrollmentRepositoryImpl,
    private val enrollmentDao: EnrollmentDao // 👈 Necesitas inyectar el DAO
) {
    suspend operator fun invoke(
        studentId: String,
        forceRefresh: Boolean = true // Para decidir si queremos forzar red
    ): Result<List<Enrollment>> {

        return try {
            // 1. Intentar obtener de la red
            val response = repository.getAllEnrollments(1, mapOf(
                "studentId" to studentId,
                "status" to "1",
                "limit" to "50"
            )).getOrThrow()

            val remoteData = response.docs

            // 2. Si hay éxito, guardamos en Room para uso offline futuro
            val entities = remoteData.map { it.toEntity() }
            enrollmentDao.insertAll(entities)

            // Devolvemos el modelo de dominio
            Result.success(remoteData.map { it.toDomainModel() })

        } catch (e: Exception) {
            println("GetStudentEnrollments: Error de red, cargando desde Room... ${e.message}")

            // 3. Si falla (Offline), buscamos en la base de datos local
            val localEntities = enrollmentDao.getEnrollmentsByStudent(studentId)

            if (localEntities.isNotEmpty()) {
                Result.success(localEntities.map { it.toDomain() })
            } else {
                // Si no hay nada ni en red ni en local, propagamos el error
                Result.failure(e)
            }
        }
    }
}