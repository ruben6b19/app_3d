package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.*
import com.jaco.cc3d.data.network.utils.handleApiFailure
import com.jaco.cc3d.domain.models.*
import com.jaco.cc3d.domain.usecases.quizQuestion.*
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions.util.QuizQuestionFeedbackMessages
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions.util.QuizQuestionResources
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizQuestionViewModel @Inject constructor(
    private val getAllUseCase: GetAllQuizQuestions,
    private val createUseCase: CreateQuizQuestion,
    private val updateUseCase: UpdateQuizQuestion,
    private val deleteUseCase: DeleteQuizQuestion
) : ScreenModel {

    var currentLanguage by mutableStateOf("es")
        private set
    // --- Estado de la Lista ---
    var currentTemplateId by mutableStateOf("")
    var questions by mutableStateOf<List<QuizQuestion>>(emptyList())
    var isListLoading by mutableStateOf(false)

    // --- Estado del Formulario ---
    var isFormOpen by mutableStateOf(false)
    var isFormSubmitting by mutableStateOf(false)
    var questionToEdit by mutableStateOf<QuizQuestion?>(null)

    // --- Inputs del Formulario ---
    var questionTextInput by mutableStateOf("")
    var typeInput by mutableStateOf(1)
    var optionsInput = mutableStateListOf<QuizOption>()

    // --- Feedback ---
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set
    var mustLogout by mutableStateOf(false)
        private set

    // =========================================================================
    // LÓGICA DE TRADUCCIONES Y ERRORES
    // =========================================================================

    private fun getFeedbackMessages(): QuizQuestionFeedbackMessages {
        // Obtenemos las traducciones (puedes parametrizar el idioma si lo manejas globalmente)
        //return QuizQuestionResources.get("es").feedback
        return QuizQuestionResources.get(currentLanguage).feedback
    }

    private fun resetFeedback() {
        errorMessage = null
        successMessage = null
    }

    private fun handleFailure(exception: Throwable): String {
        val messages = getFeedbackMessages()

        // Implementación idéntica a QuizTemplateViewModel
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
    // LÓGICA DE CARGA
    // =========================================================================

    fun setTemplateContext(templateId: String, language: String) {
        if (currentTemplateId != templateId || currentLanguage != language) {
            currentTemplateId = templateId
            currentLanguage = language
            loadQuestions()
        }
    }

    fun loadQuestions() {
        if (currentTemplateId.isBlank()) return
        screenModelScope.launch {
            isListLoading = true
            resetFeedback()

            getAllUseCase(page = 1, quizTemplateId = currentTemplateId).onSuccess {
                questions = it.docs
            }.onFailure {
                errorMessage = handleFailure(it)
            }
            isListLoading = false
        }
    }

    // --- Gestión de Opciones ---
    fun addOption() {
        optionsInput.add(QuizOption(text = "", isCorrect = false))
    }

    fun removeOption(index: Int) {
        if (optionsInput.size > 1) {
            optionsInput.removeAt(index)
        }
    }

    fun updateOptionText(index: Int, text: String) {
        optionsInput[index] = optionsInput[index].copy(text = text)
    }

    //fun toggleOptionCorrect(index: Int) {
    //    val current = optionsInput[index]
    //    optionsInput[index] = current.copy(isCorrect = !current.isCorrect)
    //}

    // 1. Lógica para que solo haya una elección correcta (Radio Button Logic)
    fun toggleOptionCorrect(index: Int) {
        // Recorremos la lista y ponemos todas en false, excepto la que clickeamos
        optionsInput.forEachIndexed { i, _ ->
            optionsInput[i] = optionsInput[i].copy(isCorrect = i == index)
        }
    }

    // 2. Preset para 5 opciones (A, B, C, D, E)
    fun setFiveOptionsPreset() {
        optionsInput.clear()
        val labels = listOf("", "", "", "", "")
        labels.forEach { label ->
            optionsInput.add(QuizOption(text = "", isCorrect = false))
        }
        // Ponemos la primera como correcta por defecto para evitar errores de validación
        toggleOptionCorrect(0)
    }

    // 3. Preset para Verdadero/Falso
    fun setTrueFalsePreset() {
        optionsInput.clear()
        val trueText = if (currentLanguage == "es") "Verdadero" else "True"
        val falseText = if (currentLanguage == "es") "Falso" else "False"

        optionsInput.add(QuizOption(text = trueText, isCorrect = true))
        optionsInput.add(QuizOption(text = falseText, isCorrect = false))
    }
    // =========================================================================
    // LÓGICA CRUD
    // =========================================================================

    fun openCreateForm() {
        questionToEdit = null
        questionTextInput = ""
        typeInput = 1
        optionsInput.clear()
        addOption()
        resetFeedback()
        isFormOpen = true
    }

    fun openEditForm(question: QuizQuestion) {
        questionToEdit = question
        questionTextInput = question.questionText
        typeInput = question.questionType
        optionsInput.clear()
        optionsInput.addAll(question.options)
        resetFeedback()
        isFormOpen = true
    }

    fun saveQuestion() {
        val messages = getFeedbackMessages()

        // Validación básica
        if (questionTextInput.isBlank()) {
            errorMessage = messages.requiredFields
            return
        }
        if (optionsInput.none { it.isCorrect }) {
            errorMessage = "Debes marcar una opción como correcta"
            return
        }

        screenModelScope.launch {
            resetFeedback()
            isFormSubmitting = true

            val request = QuizQuestionDomainRequest(
                quizTemplateId = currentTemplateId,
                questionText = questionTextInput,
                questionType = typeInput,
                options = optionsInput.toList(),
            )

            val result = if (questionToEdit != null) {
                updateUseCase(questionToEdit!!.id, request)
            } else {
                createUseCase(request)
            }

            result.onSuccess {
                loadQuestions()
                isFormOpen = false
                successMessage = messages.successCreate // O successUpdate según corresponda
            }.onFailure {
                errorMessage = handleFailure(it)
            }

            isFormSubmitting = false
        }
    }

    fun deleteQuestion(id: String) {
        val messages = getFeedbackMessages()
        screenModelScope.launch {
            resetFeedback()
            deleteUseCase(id).onSuccess {
                questions = questions.filter { it.id != id }
                successMessage = messages.successDelete
            }.onFailure {
                errorMessage = handleFailure(it)
            }
        }
    }
}