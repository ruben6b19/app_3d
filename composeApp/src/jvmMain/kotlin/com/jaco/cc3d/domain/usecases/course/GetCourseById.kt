package com.jaco.cc3d.domain.usecases.course

import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.repositories.course.CourseRepository
import jakarta.inject.Inject

class GetCourseById @Inject constructor(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(courseId: String): Result<Course> {
        return repository.getCourseById(courseId)
    }
}