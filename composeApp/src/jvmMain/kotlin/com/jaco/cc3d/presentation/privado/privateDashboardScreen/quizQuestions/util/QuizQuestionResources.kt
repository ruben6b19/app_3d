package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions.util

import java.util.Locale

data class QuizQuestionContent(
    val list: QuizQuestionListStrings,
    val form: QuizQuestionFormStrings,
    val feedback: QuizQuestionFeedbackMessages
)

data class QuizQuestionListStrings(
    val titleScreen: (String) -> String,
    val emptyListMessage: String,
    val retryButton: String,
    val fabCreate: String,
    val backButton: String,
    val textPrefix: String,
    val typePrefix: String,
    val optionsCountPrefix: String,
    val editAction: String,
    val deleteAction: String,
    val deleteDialogTitle: String,
    val deleteDialogMessage: String,
    val cancelAction: String,
    val deleteConfirmAction: String
)

data class QuizQuestionFormStrings(
    val titleCreate: String,
    val titleEdit: String,
    val textFieldLabel: String,
    val typeFieldLabel: String,
    val optionsSectionTitle: String,
    val addOptionButton: String,
    val isCorrectLabel: String,
    val optionTextPlaceholder: String,
    val buttonSave: String,
    val buttonCancel: String,
    val quickFormatsTitle: String,
    val presetTrueFalse: String,
    val presetFiveOptions: String,
    val typeMultipleChoice: String,
    val typeTrueFalse: String,
    val typeShortAnswer: String
)

data class QuizQuestionFeedbackMessages(
    val requiredFields: String,
    val minOptionsError: String,
    val noCorrectOptionError: String,
    val sessionExpired: String,
    val unknownError: String,
    val successDelete: String,
    val successCreate: String,
    val successUpdate: String
)

object QuizQuestionResources {
    private val ES = QuizQuestionContent(
        list = QuizQuestionListStrings(
            titleScreen = { name -> "Preguntas: $name" },
            emptyListMessage = "No hay preguntas en esta plantilla.",
            retryButton = "Reintentar",
            fabCreate = "Nueva Pregunta",
            backButton = "Volver",
            textPrefix = "Pregunta:",
            typePrefix = "Tipo:",
            optionsCountPrefix = "Opciones:",
            editAction = "Editar",
            deleteAction = "Eliminar",
            deleteDialogTitle = "Eliminar Pregunta",
            deleteDialogMessage = "¿Estás seguro de que deseas eliminar esta pregunta?",
            cancelAction = "Cancelar",
            deleteConfirmAction = "Eliminar"
        ),
        form = QuizQuestionFormStrings(
            titleCreate = "Crear Pregunta",
            titleEdit = "Editar Pregunta",
            textFieldLabel = "Texto de la Pregunta",
            typeFieldLabel = "Tipo de Pregunta",
            optionsSectionTitle = "Opciones de Respuesta",
            addOptionButton = "Añadir Opción",
            isCorrectLabel = "Correcta",
            optionTextPlaceholder = "Texto de la opción...",
            buttonSave = "Guardar",
            buttonCancel = "Cancelar",
            quickFormatsTitle = "Formatos rápidos",
            presetTrueFalse = "V / F",
            presetFiveOptions = "5 Opciones",
            typeMultipleChoice = "Opción Múltiple",
            typeTrueFalse = "Verdadero/Falso",
            typeShortAnswer = "Respuesta Corta"
        ),
        feedback = QuizQuestionFeedbackMessages(
            requiredFields = "El texto y la puntuación son obligatorios.",
            minOptionsError = "Debes añadir al menos 2 opciones.",
            noCorrectOptionError = "Debes marcar una opción como correcta.",
            sessionExpired = "Sesión expirada.",
            unknownError = "Error inesperado.",
            successDelete = "Pregunta eliminada.",
            successCreate = "Pregunta creada.",
            successUpdate = "Pregunta actualizada."
        )
    )

    private val EN = QuizQuestionContent(
        list = QuizQuestionListStrings(
            titleScreen = { name -> "Questions: $name" },
            emptyListMessage = "There are no questions in this template.",
            retryButton = "Retry",
            fabCreate = "New Question",
            backButton = "Back",
            textPrefix = "Question:",
            typePrefix = "Type:",
            optionsCountPrefix = "Options:",
            editAction = "Edit",
            deleteAction = "Delete",
            deleteDialogTitle = "Delete Question",
            deleteDialogMessage = "Are you sure you want to delete this question?",
            cancelAction = "Cancel",
            deleteConfirmAction = "Delete"
        ),
        form = QuizQuestionFormStrings(
            titleCreate = "Create Question",
            titleEdit = "Edit Question",
            textFieldLabel = "Question Text",
            typeFieldLabel = "Question Type",
            optionsSectionTitle = "Answer Options",
            addOptionButton = "Add Option",
            isCorrectLabel = "Correct",
            optionTextPlaceholder = "Option text...",
            buttonSave = "Save",
            buttonCancel = "Cancel",
            quickFormatsTitle = "Quick Formats",
            presetTrueFalse = "T / F",
            presetFiveOptions = "5 Options",
            typeMultipleChoice = "Multiple Choice",
            typeTrueFalse = "True/False",
            typeShortAnswer = "Short Answer"
        ),
        feedback = QuizQuestionFeedbackMessages(
            requiredFields = "Text and score are required.",
            minOptionsError = "You must add at least 2 options.",
            noCorrectOptionError = "You must mark one option as correct.",
            sessionExpired = "Session expired.",
            unknownError = "Unexpected error.",
            successDelete = "Question deleted.",
            successCreate = "Question created.",
            successUpdate = "Question updated."
        )
    )

    fun get(langCode: String): QuizQuestionContent =
        if (langCode.lowercase() == "es") ES else EN
}