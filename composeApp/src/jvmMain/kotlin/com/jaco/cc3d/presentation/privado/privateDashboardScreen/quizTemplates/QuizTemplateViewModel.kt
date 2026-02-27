package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaco.cc3d.data.network.utils.handleApiFailure
import com.jaco.cc3d.domain.models.QuizTemplate
import com.jaco.cc3d.domain.models.QuizTemplateDomainRequest
import com.jaco.cc3d.domain.usecases.quizTemplate.CreateQuizTemplate
import com.jaco.cc3d.domain.usecases.quizTemplate.DeleteQuizTemplate
import com.jaco.cc3d.domain.usecases.quizTemplate.GetAllQuizTemplates
import com.jaco.cc3d.domain.usecases.quizTemplate.UpdateQuizTemplate
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates.util.QuizTemplateFeedbackMessages
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates.util.QuizTemplateResources
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class QuizTemplateUiMode {
    LIST, CREATE, EDIT
}

class QuizTemplateViewModel @Inject constructor(
    private val getAllQuizTemplatesUseCase: GetAllQuizTemplates,
    private val createQuizTemplateUseCase: CreateQuizTemplate,
    private val updateQuizTemplateUseCase: UpdateQuizTemplate,
    private val deleteQuizTemplateUseCase: DeleteQuizTemplate,
    // REMOVED: private val subjectsStore: SubjectsStore
) : ScreenModel {

    // =========================================================================
    // ESTADO DE LA LISTA Y CONTEXTO
    // =========================================================================

    // >> NUEVO: Contexto de Materia
    var currentSubjectId by mutableStateOf("")
        private set

    var quizTemplates by mutableStateOf<List<QuizTemplate>>(emptyList())
        private set

    var uiMode by mutableStateOf(QuizTemplateUiMode.LIST)

    var isListLoading by mutableStateOf(false)
        private set

    var templateIdBeingDeleted by mutableStateOf<String?>(null)
        private set

    // =========================================================================
    // ESTADO DEL FORMULARIO
    // =========================================================================

    var isFormOpen by mutableStateOf(false)
        private set

    var templateToEdit by mutableStateOf<QuizTemplate?>(null)
        private set

    val isEditing: Boolean
        get() = templateToEdit != null

    var isFormSubmitting by mutableStateOf(false)
        private set

    // Inputs
    // REMOVED: var subjectIdInput (Ahora es implícito: currentSubjectId)
    var nameInput by mutableStateOf("")
    var languageInput by mutableStateOf("es") // Valor por defecto

    // Errores de validación
    // REMOVED: var subjectIdError
    var nameError by mutableStateOf<String?>(null)


    // =========================================================================
    // FEEDBACK (Compartido)
    // =========================================================================

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var mustLogout by mutableStateOf(false)
        private set
    // =========================================================================

    // =========================================================================
    // INICIALIZACIÓN
    // =========================================================================

    init {
        // La carga inicial se realiza en setSubjectContext
    }

    // >> NUEVO: Función para establecer el contexto de la materia
    fun setSubjectContext(subjectId: String) {
        if (currentSubjectId != subjectId) {
            currentSubjectId = subjectId
            loadQuizTemplates()
        }
    }

    private fun getFeedbackMessages(): QuizTemplateFeedbackMessages {
        return QuizTemplateResources.get("es").feedback
    }

    private fun resetFeedback() {
        errorMessage = null
        successMessage = null
    }

    private fun resetFormErrors() {
        // REMOVED: subjectIdError
        nameError = null
        //languageInput = "es"
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

    fun clearErrorMessage() {
        errorMessage = null
    }

    fun clearSuccessMessage() {
        successMessage = null
    }

    fun onLogoutHandled() {
        mustLogout = false
    }
    // =========================================================================


    // =========================================================================
    // LÓGICA DE CARGA
    // =========================================================================

    fun loadQuizTemplates(page: Int = 1, limit: Int = 20) { // Removida la necesidad de `filters` explícitos
        // Solo carga si hay un contexto de materia establecido
        if (isListLoading || currentSubjectId.isBlank()) return

        screenModelScope.launch {
            isListLoading = true
            resetFeedback()

            // 💡 Filtrar por el ID de la materia actual
           // val filters = mapOf("subject" to currentSubjectId)

            getAllQuizTemplatesUseCase(page, subjectId = currentSubjectId).onSuccess { pagination ->
                quizTemplates = pagination.docs
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
                quizTemplates = emptyList()
            }
            isListLoading = false
        }
    }

    // REMOVED: observeSubjectsStore()
    // REMOVED: retryLoadSubjects()


    // =========================================================================
    // LÓGICA DEL FORMULARIO
    // =========================================================================

    private fun resetFormInputs() {
        nameInput = ""
    }

    // Actualiza tus funciones de abrir formulario:
    fun openCreateForm() {
        templateToEdit = null
        resetFormInputs()
        resetFormErrors()
        uiMode = QuizTemplateUiMode.CREATE
        isFormOpen = true // Mantener por compatibilidad si se usa en otros lados
    }

    fun openEditForm(template: QuizTemplate) {
        templateToEdit = template
        isFormOpen = true
        resetFormErrors()
        uiMode = QuizTemplateUiMode.EDIT
        // El subjectId del template DEBE coincidir con currentSubjectId (validación implícita)
        nameInput = template.name
        languageInput = template.language
    }

    fun closeForm() {
        uiMode = QuizTemplateUiMode.LIST
        isFormOpen = false
        templateToEdit = null
        resetFormInputs()
        resetFormErrors()
    }

    // =========================================================================
    // LÓGICA CRUD
    // =========================================================================

    private fun validateForm(): Boolean {
        resetFormErrors()
        var isValid = true
        val texts = getFeedbackMessages()

        // REMOVED: Validación de subjectIdInput

        if (nameInput.isBlank()) {
            nameError = texts.requiredFields
            isValid = false
        }

        if (!isValid) {
            errorMessage = texts.requiredFields
        }
        return isValid
    }

    fun saveQuizTemplate() {
        if (!validateForm() || isFormSubmitting) return

        screenModelScope.launch {
            resetFeedback()
            isFormSubmitting = true

            val request = QuizTemplateDomainRequest(
                subjectId = currentSubjectId, // 💡 Usar el subjectId de contexto
                name = nameInput,
                language = languageInput,
                status = templateToEdit?.status
            )

            val result = if (isEditing && templateToEdit != null) {
                updateQuizTemplateUseCase(templateToEdit!!.id, request)
            } else {
                createQuizTemplateUseCase(request)
            }

            result.onSuccess { updatedTemplate ->
                if (isEditing) {
                    quizTemplates = quizTemplates.map {
                        if (it.id == updatedTemplate.id) updatedTemplate else it
                    }
                    successMessage = "success_update"
                } else {
                    quizTemplates = listOf(updatedTemplate) + quizTemplates
                    successMessage = "success_create"
                }
                closeForm()

            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
                // Se mantiene la lógica de error de nombre único si viene del backend
                if (errorMessage == getFeedbackMessages().errorUniqueName) {
                    errorMessage = getFeedbackMessages().errorUniqueName
                }
            }
            isFormSubmitting = false
        }
    }

    fun deleteQuizTemplate(templateId: String) {
        screenModelScope.launch {
            resetFeedback()
            templateIdBeingDeleted = templateId

            deleteQuizTemplateUseCase(templateId).onSuccess {
                quizTemplates = quizTemplates.filter { it.id != templateId }
                successMessage = "success_delete"
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            templateIdBeingDeleted = null
        }
    }
}