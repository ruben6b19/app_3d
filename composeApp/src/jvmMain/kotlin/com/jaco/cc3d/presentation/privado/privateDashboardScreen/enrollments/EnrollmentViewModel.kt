package com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import com.jaco.cc3d.data.network.utils.handleApiFailure
import com.jaco.cc3d.domain.models.Enrollment
import com.jaco.cc3d.domain.models.EnrollmentDomainRequest
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.domain.usecases.enrollment.CreateEnrollment
import com.jaco.cc3d.domain.usecases.enrollment.DeleteEnrollment
import com.jaco.cc3d.domain.usecases.enrollment.GetAllEnrollments
import com.jaco.cc3d.domain.usecases.user.GetUsers
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments.util.EnrollmentFeedbackMessages
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments.util.EnrollmentResources
import kotlinx.coroutines.launch
import javax.inject.Inject

class EnrollmentViewModel @Inject constructor(
    private val getEnrollmentsUseCase: GetAllEnrollments,
    private val createEnrollmentUseCase: CreateEnrollment,
    private val deleteEnrollmentUseCase: DeleteEnrollment,
    // Necesitamos obtener usuarios para mostrar nombres y llenar el selector
    private val getUsersUseCase: GetUsers
) : ScreenModel {

    // --- Configuración e Idioma ---
    var lang by mutableStateOf("en")

    // Filtros de contexto
    var courseId by mutableStateOf("")
        private set
    private var instituteId by mutableStateOf("") // Necesario para buscar estudiantes del instituto

    // --- Estado de Datos ---
    var enrollments by mutableStateOf<List<Enrollment>>(emptyList())
        private set

    // Lista completa de estudiantes del instituto (para lookup de nombres)
    private var allStudents by mutableStateOf<List<User>>(emptyList())

    // --- Estado de la UI ---
    var isFormOpen by mutableStateOf(false)
        private set
    var isEnrollmentListLoading by mutableStateOf(false) // Nuevo estado para la lista (derecha)
    var isFormSubmitting by mutableStateOf(false)
    var enrollmentIdBeingDeleted by mutableStateOf<String?>(null) // Nuevo estado
        private set

    // --- Campos del Formulario ---
    var selectedStudentId by mutableStateOf("")
        private set
    var studentSelectionError by mutableStateOf<String?>(null)
        private set

    // --- Feedback ---
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set
    var mustLogout by mutableStateOf(false)
        private set

    // 💡 PROPIEDAD COMPUTADA: Estudiantes disponibles para inscribir
    // Filtra la lista total de estudiantes, quitando los que ya están en 'enrollments'
    val availableStudentsToEnroll by derivedStateOf {
        val enrolledStudentIds = enrollments.map { it.studentId }.toSet()
        allStudents.filter { !enrolledStudentIds.contains(it.id) }
    }

    // --- Inicialización ---

    fun initialize(courseId: String, instituteId: String) {
        if (this.courseId != courseId) {
            this.courseId = courseId
            this.instituteId = instituteId
            fetchEnrollments()
            fetchAvailableStudents()
        }
    }

    private fun getFeedbackMessages(): EnrollmentFeedbackMessages {
        return EnrollmentResources.get(lang).feedback
    }

    // --- Helpers de UI para la Lista ---

    fun getStudentNameById(studentId: String): String {
        return allStudents.find { it.id == studentId }?.fullName ?: "Cargando..."
    }

    fun getStudentEmailById(studentId: String): String {
        return allStudents.find { it.id == studentId }?.email ?: ""
    }

    // --- Navegación Interna ---

    fun openForm() {
        resetForm()
        isFormOpen = true
    }

    fun closeForm() {
        resetForm()
        isFormOpen = false
    }

    private fun resetForm() {
        selectedStudentId = ""
        studentSelectionError = null
        resetFeedback()
    }

    fun onStudentSelected(newId: String) {
        selectedStudentId = newId
        if (newId.isNotBlank()) studentSelectionError = null
    }

    // --- Feedback Helpers ---

    fun onLogoutHandled() {
        mustLogout = false
    }

    fun clearSuccessMessage() {
        successMessage = null
    }

    fun clearErrorMessage() {
        errorMessage = null
    }

    private fun resetFeedback() {
        // No reseteamos isLoading aquí para evitar parpadeos si se llama dentro de una corrutina de carga
        successMessage = null
        errorMessage = null
        studentSelectionError = null
    }

    private fun handleFailure(exception: Throwable): String {
        val messages = getFeedbackMessages()
        return handleApiFailure(
            exception = exception,
            sessionExpiredMessage = messages.sessionExpired,
            unknownErrorMessage = messages.unknownError,
            onSessionExpired = { mustLogout = true }
        )
    }

    // --- CRUD ---

    fun fetchEnrollments() {
        if (courseId.isBlank()) return

        screenModelScope.launch {
            isEnrollmentListLoading = true
            getEnrollmentsUseCase(courseId).onSuccess { list ->
                enrollments = list
                errorMessage = null
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isEnrollmentListLoading = false
        }
    }

    private fun fetchAvailableStudents() {
        if (instituteId.isBlank()) return

        // Asumimos que getUsersUseCase trae todos los usuarios y filtramos por rol de estudiante (0)
        // O idealmente el backend tiene un filtro. Aquí filtramos en cliente por seguridad.
        val STUDENT_ROLE = 0

        screenModelScope.launch {
            // Nota: No ponemos isLoading global aquí para no bloquear la vista si los enrollments ya cargaron
            getUsersUseCase(instituteId).onSuccess { users ->
                allStudents = users.filter { it.role.contains(STUDENT_ROLE) }
            }.onFailure {
                // Fallo silencioso o mostrar error no bloqueante
            }
        }
    }

    fun createEnrollment() {
        if (selectedStudentId.isBlank()) {
            studentSelectionError = getFeedbackMessages().requiredStudent
            return
        }

        screenModelScope.launch {
            resetFeedback()
            isFormSubmitting = true

            val request = EnrollmentDomainRequest(
                studentId = selectedStudentId,
                courseId = courseId
            )

            createEnrollmentUseCase(request).onSuccess { newEnrollment ->
                // Actualizamos la lista localmente
                enrollments = listOf(newEnrollment) + enrollments
                successMessage = getFeedbackMessages().successEnroll
                closeForm()
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isFormSubmitting = false
        }
    }

    fun deleteEnrollment(enrollmentId: String) {
        screenModelScope.launch {
            resetFeedback()
            //isLoading = true
            enrollmentIdBeingDeleted = enrollmentId
            deleteEnrollmentUseCase(enrollmentId).onSuccess {
                // Removemos localmente
                enrollments = enrollments.filter { it.id != enrollmentId }
                successMessage = getFeedbackMessages().successRemove
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            enrollmentIdBeingDeleted = null
            //isLoading = false
        }
    }
}