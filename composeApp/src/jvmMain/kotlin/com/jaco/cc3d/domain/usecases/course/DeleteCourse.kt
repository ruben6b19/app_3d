package com.jaco.cc3d.domain.usecases.course

import com.jaco.cc3d.domain.repositories.course.CourseRepository
import javax.inject.Inject

/**
 * Use Case para eliminar un curso por ID.
 */
class DeleteCourse @Inject constructor(
    private val repository: CourseRepository
) {
    /**
     * Invoca el caso de uso para eliminar un curso.
     * @param courseId El ID del curso a eliminar.
     * @return Result<Unit> indicando éxito o fracaso de la operación.
     */
    suspend operator fun invoke(courseId: String): Result<Unit> {
        // El Use Case solo requiere el ID. Llama directamente al repositorio.
        return repository.deleteCourse(courseId)
    }
}