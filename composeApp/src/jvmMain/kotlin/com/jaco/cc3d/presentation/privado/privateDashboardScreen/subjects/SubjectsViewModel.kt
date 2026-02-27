package com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaco.cc3d.domain.models.Subject
import com.jaco.cc3d.domain.models.SubjectDomainRequest
import com.jaco.cc3d.domain.usecases.subject.CreateSubject
import com.jaco.cc3d.domain.usecases.subject.DeleteSubject
import com.jaco.cc3d.domain.usecases.subject.GetSubjects
import com.jaco.cc3d.domain.usecases.subject.UpdateSubject
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jaco.cc3d.utils.ValidationRegex
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.util.SubjectFeedbackMessages
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.util.SubjectsResources
import com.jaco.cc3d.data.network.utils.handleApiFailure
import com.jaco.cc3d.domain.models.Slide
import com.jaco.cc3d.domain.usecases.subjectConfig.SyncSubjectContent
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.ensureMarkdownIds
import java.io.File

enum class SubjectsUiMode {
    LIST, CREATE, EDIT
}

class SubjectsViewModel @Inject constructor(
    private val getSubjectsUseCase: GetSubjects,
    private val createSubjectUseCase: CreateSubject,
    private val updateSubjectUseCase: UpdateSubject,
    private val deleteSubjectUseCase: DeleteSubject,
    private val subjectsStore: SubjectsStore,
    private val syncSubjectContent: SyncSubjectContent,
) : ScreenModel {

    var hasFetched by mutableStateOf(false)
        private set
    var lang by mutableStateOf("en")

    // --- Estado de la UI ---
    val subjects by subjectsStore::subjects // Proxy para leer el estado del Store
    val isListLoading by subjectsStore::isLoading // Proxy para leer el estado de carga del Store
    var uiMode by mutableStateOf(SubjectsUiMode.LIST)
        private set
    var selectedSubject by mutableStateOf<Subject?>(null)
        private set


    // --- Feedback ---
    var isCrudLoading by mutableStateOf(false) // Usamos esto en resetFeedback y CRUD
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set
    var mustLogout by mutableStateOf(false)
        private set

    // --- Campos del Formulario ---
    var name by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set

    // --- Errores de Campos ---
    var nameError by mutableStateOf<String?>(null)
        private set
    var descriptionError by mutableStateOf<String?>(null)
        private set

    init {
        // 🚀 ¡NUEVO DISPARADOR DE CARGA!
        // Esto solo se ejecuta la primera vez que la SubjectsScreen es creada
        subjectsStore.loadSubjectsIfNecessary()
        // NOTA: Si necesitas el flag hasFetched, podrías usar subjectsStore.hasLoaded
        hasFetched = subjectsStore.hasLoaded
    }

    private fun getFeedbackMessages(): SubjectFeedbackMessages {
        return SubjectsResources.get(lang).feedback
    }

    // --- Navegación Interna ---
    fun enterCreateMode() {
        resetForm()
        uiMode = SubjectsUiMode.CREATE
    }

    fun enterEditMode(subject: Subject) {
        selectedSubject = subject
        name = subject.name
        description = subject.description ?: "" // Manejo de opcional
        uiMode = SubjectsUiMode.EDIT
    }

    fun exitFormMode() {
        resetForm()
        uiMode = SubjectsUiMode.LIST
    }

    private fun resetForm() {
        name = ""
        description = ""
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
        isCrudLoading = false
        successMessage = null
        errorMessage = null
        resetFieldErrors()
    }

    private fun resetFieldErrors() {
        nameError = null
        descriptionError = null
    }

    // --- Inputs ---
    fun onNameChange(newValue: String) {
        name = newValue
        validateInput()
    }

    fun onDescriptionChange(newValue: String) {
        description = newValue
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

    fun fetchSubjects() {
        // En lugar de llamar directamente a getSubjectsUseCase,
        // disparamos la carga en el Store (aunque esta es una carga única).
        subjectsStore.loadSubjectsIfNecessary()
        // Si necesitas forzar una recarga, SubjectsStore necesitaría una función 'reloadSubjects()'.

        // Si la función se queda, ajusta el feedback local (aunque ahora usa el Store.isLoading)
        // Puedes agregar lógica de manejo de errores si subjectsStore.loadSubjectsIfNecessary() falla.
        hasFetched = subjectsStore.hasLoaded // Actualiza el flag
    }

    fun createSubject() {
        if (!validateInput()) return

        screenModelScope.launch {
            resetFeedback()
            isCrudLoading = true
            // 💡 NOTA: Podrías usar 'subjectsStore.isLoading = true' si SubjectsStore fuera un VM con Scope CRUD

            val request = SubjectDomainRequest(
                name = name,
                description = description.ifBlank { null }
            )

            createSubjectUseCase(request).onSuccess { newSubject ->
                // 💡 ACTUALIZAR EL ESTADO DEL STORE DESPUÉS DE LA CREACIÓN EXITOSA
                subjectsStore.subjects = listOf(newSubject) + subjectsStore.subjects
                successMessage = "success_subject_created"
                exitFormMode()
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            // 💡 Control de carga local
            isCrudLoading = false
        }
    }

    fun updateSubject() {
        if (!validateInput()) return

        val subjectToUpdate = selectedSubject
        if (subjectToUpdate == null) {
            errorMessage = getFeedbackMessages().noSubjectSelected
            return
        }

        screenModelScope.launch {
            resetFeedback()
            isCrudLoading = true

            val request = SubjectDomainRequest(
                name = name,
                description = description.ifBlank { null },
            )

            updateSubjectUseCase(subjectToUpdate.id, request).onSuccess { updatedSubject ->
                // 💡 ACTUALIZAR EL ESTADO DEL STORE
                subjectsStore.subjects = subjectsStore.subjects.map { current ->
                    if (current.id == updatedSubject.id) updatedSubject else current
                }
                successMessage = "success_subject_updated"
                exitFormMode()
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isCrudLoading = false
        }
    }

    fun deleteSubject(subjectId: String) {
        screenModelScope.launch {
            resetFeedback()
            isCrudLoading = true

            deleteSubjectUseCase(subjectId).onSuccess {
                successMessage = "success_subject_deleted"
                // 💡 ACTUALIZAR EL ESTADO DEL STORE
                subjectsStore.subjects = subjectsStore.subjects.filter { it.id != subjectId }
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isCrudLoading = false
        }
    }

    fun saveMarkdownContent(file: File, subjectId: String, currentSlides: List<Slide>) {
        //val subjectId = selectedSubject?.id ?: return

        screenModelScope.launch {
            resetFeedback()
            isCrudLoading = true
            try {
                // 1. 🎯 LA CLAVE: Leemos el contenido actual del archivo
                val rawText = file.readText()

                // 2. Inyectamos los IDs (Si no los tiene, los crea; si los tiene, los deja)
                val enrichedMarkdown = ensureMarkdownIds(rawText)

                // 3. Sobreescribimos el archivo local con el texto "enriquecido"
                // Esto es importante para que lo que se sincronice sea la versión con IDs
                file.writeText(enrichedMarkdown)

                // 4. Sincronizamos el archivo ya etiquetado
                syncSubjectContent(
                    file = file,
                    subjectId = subjectId,
                    slides = currentSlides,
                    commitMessage = "Actualización con IDs persistentes"
                ).onSuccess { config ->
                    successMessage = "Contenido guardado y etiquetado con éxito"
                    uiMode = SubjectsUiMode.LIST
                }.onFailure { exception ->
                    errorMessage = "Fallo en la sincronización: ${exception.message}"
                }

            } catch (e: Exception) {
                errorMessage = "Error al procesar el archivo: ${e.message}"
            } finally {
                isCrudLoading = false
            }
        }
    }

    // --- Validación ---
    fun validateInput(): Boolean {
        resetFieldErrors()
        var isValid = true
        val messages = getFeedbackMessages()

        // Validar Nombre
        if (name.isBlank()) {
            nameError = messages.requiredName
            isValid = false
        } else if (!ValidationRegex.VALIDATE_TEXT.matches(name)) {
            nameError = messages.invalidName
            isValid = false
        }

        // Validar Descripción (Opcional, pero si existe, verificar regex)
        if (description.isNotBlank() && !ValidationRegex.VALIDATE_DESCRIPTION.matches(description)) {
            // Asumiendo que tienes VALIDATE_DESCRIPTION o usa VALIDATE_TEXT
            descriptionError = messages.invalidDescription
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