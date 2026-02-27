package com.jaco.cc3d.domain.usecases.course

import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.data.repositories.course.CourseRepositoryImpl
import com.jaco.cc3d.domain.models.Course
import javax.inject.Inject
import com.jaco.cc3d.data.mappers.toDomain // La que creamos arriba
import java.io.IOException

class GetCoursesByTeacher @Inject constructor(
    private val repository: CourseRepositoryImpl
) {
    suspend operator fun invoke(
        teacherId: String,
        page: Int = 1,
        limit: Int = 50,
    ): Result<List<Course>> {
        val filtersMap = mapOf(
            "teacherId" to teacherId,
            "limit" to limit.toString(),
            "status" to "1"
        )

        // 1. Intentar siempre la API primero
        val networkResult = repository.getAllCourses(page, filtersMap)

        return networkResult.fold(
            onSuccess = { paginationResponse ->
                // ✅ Éxito: El repositorio ya hizo el upsert a Room internamente
                println("Cargado de api:")
                Result.success(paginationResponse.docs.map { it.toDomainModel() })
            },
            onFailure = { error ->
                // ❌ Fallo de red (o cualquier error): Intentamos Modo Offline
                println("Fallo de red detectado: ${error.message}. Intentando cargar de Room...")

                val localEntities = repository.getLocalCoursesByTeacher(teacherId)

                if (localEntities.isNotEmpty()) {
                    // ✅ Tenemos datos en Room: Los devolvemos para que el profe trabaje
                    // Podrías filtrar aquí por teacherId si la DB guarda varios profes
                    Result.success(localEntities.map { it.toDomain() })
                } else {
                    // ❌ Ni red ni Room: Devolvemos el error original
                    Result.failure(error)
                }
            }
        )
    }
}