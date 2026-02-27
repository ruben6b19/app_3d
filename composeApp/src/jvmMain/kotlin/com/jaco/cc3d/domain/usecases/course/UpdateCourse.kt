package com.jaco.cc3d.domain.usecases.course

import com.jaco.cc3d.data.mappers.toDataRequest
import com.jaco.cc3d.data.mappers.toDomainModel
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.models.CourseDomainRequest
import com.jaco.cc3d.data.repositories.course.CourseRepositoryImpl
import javax.inject.Inject

/**
 * Use Case para actualizar un curso existente.
 */
class UpdateCourse @Inject constructor(
    private val repository: CourseRepositoryImpl
) {
    /**
     * Invoca el caso de uso para actualizar un curso.
     * @param courseId El ID del curso a actualizar.
     * @param request El modelo de solicitud de dominio con los datos actualizados.
     * @return Result<Course> El modelo de dominio [Course] actualizado o un error.
     */
    suspend operator fun invoke(courseId: String, request: CourseDomainRequest): Result<Course> {
        // 1. Mapear el modelo de dominio a DTO de solicitud de datos
        val courseRequestDto = request.toDataRequest()

        // 2. Llamar al repositorio y manejar la respuesta
        return repository.updateCourse(courseId, courseRequestDto)
            .mapCatching { dto ->
                // 3. Mapeamos el DTO de respuesta (el curso actualizado) a un modelo de Dominio
                dto.toDomainModel()
            }
    }
}