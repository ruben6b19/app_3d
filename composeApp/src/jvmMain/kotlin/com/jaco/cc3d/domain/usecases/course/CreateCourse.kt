package com.jaco.cc3d.domain.usecases.course

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.models.CourseDomainRequest
import com.jaco.cc3d.data.repositories.course.CourseRepositoryImpl
import javax.inject.Inject

/**
 * Use Case para crear un nuevo curso.
 * Recibe el modelo de Dominio [CourseDomainRequest] y lo transforma a un DTO (Request) para el Repositorio,
 * luego mapea el DTO de respuesta de vuelta al modelo de Dominio [Course].
 */
class CreateCourse @Inject constructor(
    private val repository: CourseRepositoryImpl
) {
    /**
     * Invoca el caso de uso para crear un nuevo curso.
     * @param request El modelo de solicitud de dominio con los datos del nuevo curso.
     * @return Result que contiene el modelo de dominio [Course] creado, o un error.
     */
    suspend operator fun invoke(request: CourseDomainRequest): Result<Course> {
        val courseRequestDto = request.toDataRequest()

        return repository.createCourse(courseRequestDto)
            .mapCatching { dto ->
                // 3. Mapear el DTO de respuesta (el curso recién creado) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}