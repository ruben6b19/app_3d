package com.jaco.cc3d.presentation.privado.teacherDashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.network.utils.handleApiFailure
import com.jaco.cc3d.data.local.cache.FileCacheManager.isDownloaded
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.usecases.course.GetCoursesByTeacher // Asumiendo que existe este caso de uso
import com.jaco.cc3d.presentation.privado.teacherDashboard.util.TeacherDashboardResources
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeacherDashboardViewModel @Inject constructor(
    private val getCoursesByTeacher: GetCoursesByTeacher,
    private val tokenManager: EncryptedDesktopTokenManager,
) : ScreenModel {

    var lang by mutableStateOf("es")
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var teacherName by mutableStateOf("")
        private set

    var mustLogout by mutableStateOf(false)
        private set

    var downloadedSubjects by mutableStateOf<Set<String>>(emptySet())
        private set

    init {
        loadTeacherData()
        fetchMyCourses()
    }

    private fun loadTeacherData() {
        val user = tokenManager.getUserData()
        teacherName = user?.fullName ?: "Profesor"
    }

    private fun handleFailure(exception: Throwable): String {
        val feedback = TeacherDashboardResources.get(lang).feedback
        println("handleFailure")
        return handleApiFailure(
            exception = exception,
            sessionExpiredMessage = feedback.sessionExpired,
            unknownErrorMessage = feedback.unknownError,
            onSessionExpired = {
                println("handleFailure mustlogout")
                mustLogout = true }
        )
    }

    fun onLogoutHandled() { mustLogout = false }

    fun clearErrorMessage() {
        errorMessage = null
    }

    fun fetchMyCourses() {
        val teacherId = tokenManager.getUserData()?._id ?: ""
        println("Fetching courses for $teacherId")
        screenModelScope.launch {
            isLoading = true
            errorMessage = null
            getCoursesByTeacher(teacherId = teacherId)
                .onSuccess { list ->
                    courses = list
                    checkDownloadedFiles(list)
                }
                .onFailure { exception -> errorMessage = handleFailure(exception) }
            isLoading = false
        }
    }

    private fun checkDownloadedFiles(courseList: List<Course>) {
        val downloaded = courseList.filter { course ->
            isDownloaded(course.subjectId ?: "", course.contentUrl)
        }.map { it.subjectId }.toSet()

        downloadedSubjects = downloaded
    }
}