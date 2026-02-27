package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates.util

import java.util.Locale

// 1. Data Class Padre
data class QuizTemplateContent(
    val list: QuizTemplateListStrings,
    val form: QuizTemplateFormStrings,
    val feedback: QuizTemplateFeedbackMessages
)

// 2. Sub-clases
data class QuizTemplateListStrings(
    val titleScreen: String,
    val emptyListMessage: String,
    val retryButton: String,
    val fabCreate: String,
    val backButton: String,
    val namePrefix: String,
    val subjectPrefix: String,
    val categoryPrefix: String,
    val maxScorePrefix: String,
    val languagePrefix: String,
    val editAction: String,
    val deleteAction: String,
    val questionsAction: String,
    val moreActions: String,
    val deleteDialogTitle: String,
    val deleteDialogMessage: (String) -> String,
    val cancelAction: String,
    val deleteConfirmAction: String
)

data class QuizTemplateFormStrings(
    val titleRegister: String, // << AÑADIDO: Consistente con Subjects
    val titleEdit: (String) -> String, // << CAMBIADO A FUNCIÓN: Consistente con Subjects
    val nameLabel: String,
    val subjectLabel: String,
    val categoryLabel: String,
    val maxScoreLabel: String,
    val languageLabel: String,
    val namePlaceholder: String,
    val maxScorePlaceholder: String,
    val buttonSave: String,
    val buttonCancel: String,
    val noSubjectsFound: String,
    val selectCategory: String,
    val dailyCategory: String,
    val finalCategory: String,
    val maxScoreHelper: String
)

data class QuizTemplateFeedbackMessages(
    val requiredFields: String,
    val invalidScore: String,
    val sessionExpired: String,
    val unknownError: String,
    val successDelete: String,
    val successCreate: String,
    val successUpdate: String,
    val errorUniqueName: String
)

// 3. Objeto Singleton con las traducciones
object QuizTemplateResources {

    private val ES = QuizTemplateContent(
        list = QuizTemplateListStrings(
            titleScreen = "Gestión de Plantillas de Quiz",
            emptyListMessage = "No hay plantillas de quiz creadas.",
            retryButton = "Reintentar Carga",
            fabCreate = "Crear Plantilla",
            backButton = "Botón de retroceso",
            namePrefix = "Nombre:",
            subjectPrefix = "Materia:",
            categoryPrefix = "Categoría:",
            maxScorePrefix = "Puntuación Máx:",
            languagePrefix = "Idioma:",
            editAction = "Editar",
            deleteAction = "Eliminar",
            questionsAction = "Preguntas",
            moreActions = "Más Acciones",
            deleteDialogTitle = "Confirmar Eliminación",
            deleteDialogMessage = { name -> "¿Seguro que quieres eliminar la plantilla \"$name\"?" },
            cancelAction = "Cancelar",
            deleteConfirmAction = "Eliminar"
        ),
        form = QuizTemplateFormStrings(
            titleRegister = "Registro de Plantilla", // << ASIGNADO
            titleEdit = { name -> "Editar Plantilla: $name" }, // << ASIGNADO COMO FUNCIÓN
            nameLabel = "Nombre de la Plantilla",
            subjectLabel = "Materia",
            categoryLabel = "Categoría",
            languageLabel = "Idioma del Examen",
            maxScoreLabel = "Puntuación Máxima",
            namePlaceholder = "Ej: Tema 1 - Introducción",
            maxScorePlaceholder = "Ej: 100",
            buttonSave = "Guardar",
            buttonCancel = "Cancelar",
            noSubjectsFound = "No hay materias disponibles.",
            selectCategory = "Seleccionar Categoría",
            dailyCategory = "Diario",
            finalCategory = "Final",
            maxScoreHelper = "Puntuación total que el quiz otorga."
        ),
        feedback = QuizTemplateFeedbackMessages(
            requiredFields = "Todos los campos obligatorios deben ser llenados.",
            invalidScore = "La puntuación máxima debe ser un número entero válido.",
            sessionExpired = "Sesión expirada.",
            unknownError = "Ocurrió un error desconocido.",
            successDelete = "Plantilla eliminada exitosamente.",
            successCreate = "Plantilla creada exitosamente.",
            successUpdate = "Plantilla actualizada exitosamente.",
            errorUniqueName = "Ya existe una plantilla con este nombre."
        )
    )

    private val EN = QuizTemplateContent(
        list = QuizTemplateListStrings(
            titleScreen = "Quiz Template Management",
            emptyListMessage = "No quiz templates have been created.",
            retryButton = "Retry Load",
            fabCreate = "Create Template",
            backButton = "Back button",
            namePrefix = "Name:",
            subjectPrefix = "Subject:",
            categoryPrefix = "Category:",
            maxScorePrefix = "Max Score:",
            languagePrefix = "Language:",
            editAction = "Edit",
            deleteAction = "Delete",
            questionsAction = "Questions",
            moreActions = "More Actions",
            deleteDialogTitle = "Confirm Deletion",
            deleteDialogMessage = { name -> "Are you sure you want to delete the template \"$name\"?" },
            cancelAction = "Cancel",
            deleteConfirmAction = "Delete"
        ),
        form = QuizTemplateFormStrings(
            titleRegister = "Template Registration", // << ASIGNADO
            titleEdit = { name -> "Edit Template: $name" }, // << ASIGNADO COMO FUNCIÓN
            nameLabel = "Template Name",
            subjectLabel = "Subject",
            categoryLabel = "Category",
            languageLabel = "Exam Language",
            maxScoreLabel = "Maximum Score",
            namePlaceholder = "Ex: Topic 1 - Introduction",
            maxScorePlaceholder = "Ex: 100",
            buttonSave = "Save",
            buttonCancel = "Cancel",
            noSubjectsFound = "No subjects available.",
            selectCategory = "Select Category",
            dailyCategory = "Daily",
            finalCategory = "Final",
            maxScoreHelper = "Total score the quiz grants."
        ),
        feedback = QuizTemplateFeedbackMessages(
            requiredFields = "All required fields must be filled.",
            invalidScore = "Maximum score must be a valid integer.",
            sessionExpired = "Session expired.",
            unknownError = "An unknown error occurred.",
            successDelete = "Template deleted successfully.",
            successCreate = "Template created successfully.",
            successUpdate = "Template updated successfully.",
            errorUniqueName = "A template with this name already exists."
        )
    )

    fun get(langCode: String): QuizTemplateContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}