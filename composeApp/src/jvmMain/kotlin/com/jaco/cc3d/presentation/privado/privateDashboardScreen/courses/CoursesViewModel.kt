package com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.domain.models.CourseDomainRequest
import com.jaco.cc3d.domain.models.Subject
import com.jaco.cc3d.domain.usecases.course.CreateCourse
import com.jaco.cc3d.domain.usecases.course.DeleteCourse
import com.jaco.cc3d.domain.usecases.course.GetCourses
import com.jaco.cc3d.domain.usecases.course.UpdateCourse
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.SubjectsStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jaco.cc3d.utils.ValidationRegex
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses.util.CourseFeedbackMessages
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses.util.CoursesResources
import com.jaco.cc3d.data.network.utils.handleApiFailure
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.TeachersStore

enum class CoursesUiMode {
    LIST, CREATE, EDIT
}

class CoursesViewModel @Inject constructor(
    private val getCoursesUseCase: GetCourses,
    private val createCourseUseCase: CreateCourse,
    private val updateCourseUseCase: UpdateCourse,
    private val deleteCourseUseCase: DeleteCourse,
    private val subjectsStore: SubjectsStore,
    private val teachersStore: TeachersStore,
) : ScreenModel {

    var hasFetched by mutableStateOf(false)
        private set
    var lang by mutableStateOf("en")

    // --- Estado de la UI ---
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set
    var uiMode by mutableStateOf(CoursesUiMode.LIST)
        private set
    var selectedCourse by mutableStateOf<Course?>(null)
        private set

    // --- Feedback ---
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set
    var mustLogout by mutableStateOf(false)
        private set

    // --- Campos del Formulario ---
    // 🆕 Añadir instituteId
    var instituteId by mutableStateOf("")
        private set
    var instituteName by mutableStateOf<String?>(null)
        private set
    var subjectId by mutableStateOf("")
        private set
    var teacherId by mutableStateOf("")
        private set
    var academicYear by mutableStateOf("") // Formato YYYY
        private set
    var group by mutableStateOf("")
        private set

    // --- Errores de Campos ---
    // 🆕 Añadir instituteIdError
    var instituteIdError by mutableStateOf<String?>(null)
        private set
    var subjectIdError by mutableStateOf<String?>(null)
        private set
    var teacherIdError by mutableStateOf<String?>(null)
        private set
    var academicYearError by mutableStateOf<String?>(null)
        private set
    var groupError by mutableStateOf<String?>(null)
        private set

    var availableSubjects by mutableStateOf<List<Subject>>(emptyList())
        private set
    var availableTeachers by mutableStateOf<List<User>>(emptyList())
        private set

    // 1. Exponer estado de error de los Stores
    val subjectsLoadError by subjectsStore::loadError
    val teachersLoadError by teachersStore::loadError

    init {
        // 💡 1. INICIAR SÓLO LA OBSERVACIÓN (sin cargar la data)
        observeSubjectsStore()
        observeTeachersStore()
    }

    private fun getFeedbackMessages(): CourseFeedbackMessages {
        return CoursesResources.get(lang).feedback
    }

    fun retryLoadSubjects() {
        subjectsStore.retryLoad()
    }

    fun retryLoadTeachers() {
        teachersStore.retryLoad()
    }
    // =================================================================
    // ✅ FUNCIÓN CORREGIDA: Cambiado el nombre para evitar colisión con el setter automático
    // =================================================================
    fun initializeInstituteFilter(id: String, name: String) {
        if (this.instituteId != id) {
            this.instituteId = id
            this.instituteName = name // 💡 Guardamos el nombre

            // Una vez que tenemos el ID del instituto, cargamos los cursos
            fetchCourses()
            teachersStore.loadTeachersIfNecessary(id)
        }
    }
    // =================================================================

    private fun observeSubjectsStore() {
        screenModelScope.launch {
            // Observamos el 'mutableStateOf' del Store.
            // Esto garantiza que si SubjectsScreen dispara la carga más tarde,
            // CoursesViewModel se actualiza automáticamente.
            snapshotFlow { subjectsStore.subjects }
                .collectLatest { newSubjects ->
                    availableSubjects = newSubjects
                }
        }
    }

    private fun observeTeachersStore() {
        screenModelScope.launch {
            // Observamos el estado del TeachersStore
            snapshotFlow { teachersStore.teachers }
                .collectLatest { newTeachers ->
                    availableTeachers = newTeachers // Se actualiza reactivamente
                }
        }
    }

    // --- Navegación Interna ---
    fun enterCreateMode() {
        resetForm()
        // instituteId ya mantiene el filtro inyectado
        uiMode = CoursesUiMode.CREATE
    }

    fun enterEditMode(course: Course) {
        selectedCourse = course
        // Cargar todos los campos, incluyendo instituteId
        instituteId = course.instituteId
        subjectId = course.subjectId
        teacherId = course.teacherId
        academicYear = course.academicYear.toString() // YYYY
        group = course.group
        uiMode = CoursesUiMode.EDIT
    }

    fun exitFormMode() {
        resetForm()
        // Mantenemos el instituteId que fue inyectado (o cargado) para el filtro,
        // sin resetearlo a vacío si ya se había cargado.
        // Si quieres resetear el instituteId al salir del formulario, asegúrate de
        // re-inyectarlo o manejarlo en el ScreenModel init/setInstituteId.

        // **OPCIONAL:** Mantenemos el último ID válido.
        //instituteId = if (selectedCourse != null) selectedCourse!!.instituteId else ""

        selectedCourse = null
        uiMode = CoursesUiMode.LIST
    }

    private fun resetForm() {
        // Resetear solo los campos editables del formulario
        subjectId = ""
        teacherId = ""
        academicYear = ""
        group = ""
        resetFeedback()
        resetFieldErrors()
    }

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
        isLoading = false
        successMessage = null
        errorMessage = null
        resetFieldErrors()
    }

    private fun resetFieldErrors() {
        instituteIdError = null
        subjectIdError = null
        teacherIdError = null
        academicYearError = null
        groupError = null
    }

    // --- Inputs ---
    fun onInstituteIdChange(newValue: String) {
        instituteId = newValue
        validateInput()
    }

    fun onSubjectIdChange(newValue: String) {
        subjectId = newValue
        validateInput()
    }

    fun onTeacherIdChange(newValue: String) {
        teacherId = newValue
        validateInput()
    }

    fun onAcademicYearChange(newValue: String) {
        academicYear = newValue
        validateInput()
    }

    fun onGroupChange(newValue: String) {
        group = newValue
        validateInput()
    }

    // --- Manejo de Errores con Utils ---
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

    fun fetchCourses() {
        if (instituteId.isBlank()) {
            errorMessage = getFeedbackMessages().requiredInstitute
            return
        }

        screenModelScope.launch {
            resetFeedback()
            isLoading = true


            getCoursesUseCase(instituteId=instituteId ).onSuccess {
                // Filtramos manualmente los cursos pertenecientes al instituto inyectado
                //courses = it.filter { course -> course.instituteId == instituteId }
                courses = it
                errorMessage = null
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isLoading = false
            hasFetched = true
        }
    }

    fun createCourse() {
        if (!validateInput()) return

        screenModelScope.launch {
            resetFeedback()
            isLoading = true

            val request = CourseDomainRequest(
                instituteId = instituteId,
                subjectId = subjectId,
                teacherId = teacherId,
                academicYear = academicYear.toInt(),
                group = group
            )

            createCourseUseCase(request).onSuccess {
                courses = listOf(it) + courses
                successMessage = getFeedbackMessages().successCreate
                exitFormMode()
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isLoading = false
        }
    }

    fun updateCourse() {
        if (!validateInput()) return

        val courseToUpdate = selectedCourse
        if (courseToUpdate == null) {
            errorMessage = getFeedbackMessages().noCourseSelected
            return
        }

        screenModelScope.launch {
            resetFeedback()
            isLoading = true

            val request = CourseDomainRequest(
                instituteId = instituteId,
                subjectId = subjectId,
                teacherId = teacherId,
                academicYear = academicYear.toInt(),
                group = group
            )

            updateCourseUseCase(courseToUpdate.id, request).onSuccess { updatedCourse ->
                courses = courses.map { current ->
                    if (current.id == updatedCourse.id) updatedCourse else current
                }
                successMessage = getFeedbackMessages().successUpdate
                exitFormMode()
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isLoading = false
        }
    }

    fun deleteCourse(courseId: String) {
        screenModelScope.launch {
            resetFeedback()
            isLoading = true
            deleteCourseUseCase(courseId).onSuccess {
                successMessage = getFeedbackMessages().successDelete
                courses = courses.filter { it.id != courseId }
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isLoading = false
        }
    }

    // --- Validación ---
    fun validateInput(): Boolean {
        resetFieldErrors()
        var isValid = true
        val messages = getFeedbackMessages()

        // Validación de ID de Instituto (no vacío)
        if (instituteId.isBlank()) {
            instituteIdError = messages.requiredInstitute
            isValid = false
        }

        // Validación de ID de Materia (no vacío)
        if (subjectId.isBlank()) {
            subjectIdError = messages.requiredSubject
            isValid = false
        }

        // Validación de ID de Profesor (no vacío)
        if (teacherId.isBlank()) {
            teacherIdError = messages.requiredTeacher
            isValid = false
        }

        // Validación de Año Académico (YYYY)
        if (academicYear.isBlank()) {
            academicYearError = messages.requiredYear
            isValid = false
        } else if (!ValidationRegex.VALIDATE_FOUR_DIGITS.matches(academicYear)) {
            academicYearError = messages.invalidYear
            isValid = false
        } else {
            // 💡 Se aplica la validación de rango Int después de validar el formato String (4 dígitos)
            val yearInt = academicYear.toIntOrNull()
            val currentYear = java.time.Year.now().value
            val minYear = 1950

            // Verificar si la conversión falló o si el año está fuera de un rango razonable
            if (yearInt == null || yearInt < minYear || yearInt > currentYear + 1) {
                academicYearError = "${messages.invalidYear} (Debe estar entre $minYear y ${currentYear + 1})"
                isValid = false
            } else {
                academicYearError = null
            }
        }

        // Validación de Grupo (no vacío, usamos regex genérica de texto si no hay una específica)
        if (group.isBlank()) {
            groupError = messages.requiredGroup
            isValid = false
        } else if (!ValidationRegex.VALIDATE_TEXT.matches(group)) {
            // Asumiendo que el grupo puede ser alfanumérico simple
            groupError = "El grupo contiene caracteres inválidos."
            isValid = false
        }

        if (!isValid) {
            errorMessage = messages.formError
        } else {
            errorMessage = null
        }

        return isValid
    }
}