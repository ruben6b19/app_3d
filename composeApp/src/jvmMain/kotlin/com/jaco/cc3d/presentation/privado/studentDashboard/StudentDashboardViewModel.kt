package com.jaco.cc3d.presentation.privado.studentDashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.network.utils.handleApiFailure
import com.jaco.cc3d.data.local.cache.FileCacheManager.isDownloaded
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.models.Enrollment
import com.jaco.cc3d.domain.usecases.enrollment.GetStudentEnrollments
import com.jaco.cc3d.presentation.privado.studentDashboard.util.StudentDashboardResources
import kotlinx.coroutines.launch
import javax.inject.Inject

class StudentDashboardViewModel @Inject constructor(
    private val getStudentEnrollments: GetStudentEnrollments,
    private val tokenManager: EncryptedDesktopTokenManager,
) : ScreenModel {

    var lang by mutableStateOf("es")
    var enrollments by mutableStateOf<List<Enrollment>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var studentName by mutableStateOf("")
        private set
    var mustLogout by mutableStateOf(false)
        private set

    var downloadedSubjects by mutableStateOf<Set<String>>(emptySet())
        private set


    init {
        loadStudentData()
        fetchMyEnrollments()
    }

    private fun loadStudentData() {
        val user = tokenManager.getUserData()
        studentName = user?.fullName ?: "Estudiante"
    }

    private fun handleFailure(exception: Throwable): String {
        val feedback = StudentDashboardResources.get(lang).feedback
        return handleApiFailure(
            exception = exception,
            sessionExpiredMessage = feedback.sessionExpired,
            unknownErrorMessage = feedback.unknownError,
            onSessionExpired = {
                mustLogout = true // Esto activará la navegación al login en la View
            }
        )
    }

    fun onLogoutHandled() {
        mustLogout = false
    }

    fun clearErrorMessage() {
        errorMessage = null
    }

    fun fetchMyEnrollments() {
        val studentId = tokenManager.getUserData()?._id ?: return

        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            getStudentEnrollments(studentId = studentId)
                .onSuccess { list ->
                    enrollments = list
                    checkDownloadedFiles(list)
                }
                .onFailure { exception -> errorMessage = handleFailure(exception) }

            isLoading = false
        }
    }

    private fun checkDownloadedFiles(enrollmentList: List<Enrollment>) {
        // 1. Filtramos las matrículas que ya tienen su archivo MD en el disco
        val downloaded = enrollmentList.filter { enrollment ->
            // Usamos el ID de la materia y la URL que vienen directo en el modelo
            isDownloaded(
                subjectId = enrollment.subjectId,
                contentUrl = enrollment.contentUrl
            )
        }.map { it.subjectId }.toSet() // Guardamos un Set de los IDs de materia descargados

        // 2. Actualizamos el estado para que la UI reaccione
        downloadedSubjects = downloaded
    }
}