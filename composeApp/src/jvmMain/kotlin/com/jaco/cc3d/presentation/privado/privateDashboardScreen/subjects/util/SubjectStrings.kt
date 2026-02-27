package com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.util

import java.util.Locale

// 1. Data Class Padre
data class SubjectsContent(
    val list: SubjectListStrings,
    val form: SubjectFormStrings,
    val feedback: SubjectFeedbackMessages
)

// 2. Sub-clases
data class SubjectListStrings(
    val titleScreen: String,
    val emptyListMessage: String,
    val retryButton: String,
    val fabCreate: String,
    val backButton: String,
    val descriptionPrefix: String,
    val editAction: String,
    val deleteAction: String,
    val moreActions: String,
    val deleteDialogTitle: String,
    val deleteDialogMessage: (String) -> String,
    val cancelAction: String,
    val deleteConfirmAction: String
)

data class SubjectFormStrings(
    val titleRegister: String,
    val titleEdit: (String) -> String,
    val buttonCreate: String,
    val buttonSave: String,
    val fieldName: String,
    val fieldDescription: String,
    val success: String
)

data class SubjectFeedbackMessages(
    val requiredName: String,
    val invalidName: String,
    val invalidDescription: String, // Opcional, si validas longitud o caracteres
    val formError: String,
    val sessionExpired: String,
    val unknownError: String,
    val noSubjectSelected: String,
    val successDelete: String,
    val successCreate: String,
    val successUpdate: String
)

// 3. Objeto Singleton con las traducciones
object SubjectsResources {

    private val ES = SubjectsContent(
        list = SubjectListStrings(
            titleScreen = "Gestión de Materias",
            emptyListMessage = "No hay materias registradas.",
            retryButton = "Reintentar Carga",
            fabCreate = "Crear Nueva Materia",
            backButton = "Volver",
            descriptionPrefix = "Descripción:",
            editAction = "Editar",
            deleteAction = "Eliminar",
            moreActions = "Más Acciones",
            deleteDialogTitle = "Confirmar Eliminación",
            deleteDialogMessage = { name -> "¿Estás seguro de que quieres eliminar la materia \"$name\" de forma permanente?" },
            cancelAction = "Cancelar",
            deleteConfirmAction = "Eliminar"
        ),
        form = SubjectFormStrings(
            titleRegister = "Registrar Nueva Materia",
            titleEdit = { name -> "Editar Materia: $name" },
            buttonCreate = "Crear Materia",
            buttonSave = "Guardar Cambios",
            fieldName = "Nombre de la Materia",
            fieldDescription = "Descripción (Opcional)",
            success = "¡Operación exitosa!"
        ),
        feedback = SubjectFeedbackMessages(
            requiredName = "El nombre de la materia es obligatorio.",
            invalidName = "El nombre contiene caracteres inválidos.",
            invalidDescription = "La descripción contiene caracteres inválidos.",
            formError = "Corrige los errores en el formulario.",
            sessionExpired = "Sesión caducada. Por favor, vuelve a iniciar sesión.",
            unknownError = "Error desconocido.",
            noSubjectSelected = "No se ha seleccionado una materia para editar.",
            successDelete = "Materia eliminada exitosamente.",
            successCreate = "Materia creada exitosamente.",
            successUpdate = "Materia actualizada exitosamente."
        )
    )

    private val EN = SubjectsContent(
        list = SubjectListStrings(
            titleScreen = "Subject Management",
            emptyListMessage = "No registered subjects found.",
            retryButton = "Retry Load",
            fabCreate = "Create New Subject",
            backButton = "Go Back",
            descriptionPrefix = "Description:",
            editAction = "Edit",
            deleteAction = "Delete",
            moreActions = "More Actions",
            deleteDialogTitle = "Confirm Deletion",
            deleteDialogMessage = { name -> "Are you sure you want to permanently delete the subject \"$name\"?" },
            cancelAction = "Cancel",
            deleteConfirmAction = "Delete"
        ),
        form = SubjectFormStrings(
            titleRegister = "Register New Subject",
            titleEdit = { name -> "Edit Subject: $name" },
            buttonCreate = "Create Subject",
            buttonSave = "Save Changes",
            fieldName = "Subject Name",
            fieldDescription = "Description (Optional)",
            success = "Operation successful!"
        ),
        feedback = SubjectFeedbackMessages(
            requiredName = "The subject name is mandatory.",
            invalidName = "Name contains invalid characters.",
            invalidDescription = "Description contains invalid characters.",
            formError = "Please correct the form errors.",
            sessionExpired = "Session expired. Please log in again.",
            unknownError = "Unknown error.",
            noSubjectSelected = "No subject selected for editing.",
            successDelete = "Subject deleted successfully.",
            successCreate = "Subject created successfully.",
            successUpdate = "Subject updated successfully."
        )
    )

    fun get(langCode: String): SubjectsContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}