package com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util

import java.util.Locale

// 1. Data Class Padre que contiene todo
data class InstitutesContent(
    val list: InstituteListStrings,
    val form: InstituteFormStrings,
    val feedback: FeedbackMessages
)

// 2. Sub-clases para organizar
data class InstituteListStrings(
    val titleScreen: String,
    val emptyListMessage: String,
    val retryButton: String,
    val fabCreate: String,
    val backButton: String,
    val foundationPrefix: String,
    val cityPrefix: String,
    val languagePrefix: String,
    val usersLabel: String,
    val coursesLabel: String,
    val editAction: String,
    val deleteAction: String,
    val usersAction: String,
    val coursesAction: String,
    val deleteDialogTitle: String,
    val deleteDialogMessage: (String) -> String,
    val cancelAction: String
)

data class InstituteFormStrings(
    val titleRegister: String,
    val titleEdit: (String) -> String,
    val buttonCreate: String,
    val buttonSave: String,
    val fieldName: String,
    val fieldFoundationDate: String,
    val fieldCity: String,
    val fieldLanguage: String,
    val success: String
)

data class FeedbackMessages(
    val requiredName: String,
    val invalidName: String,
    val requiredFoundationDate: String,
    val incompleteFoundationDate: String,
    val invalidDateFormat: String,
    val requiredCity: String,
    val requiredLanguage: String,
    val formError: String,
    val sessionExpired: String,
    val unknownError: String,
    val noInstituteSelected: String,
    val successDelete: String,
    val successCreate: String,
    val successUpdate: String
)

// 3. Objeto Singleton con las traducciones
object InstitutesResources {

    private val ES = InstitutesContent(
        list = InstituteListStrings(
            titleScreen = "Gestión de Institutos",
            emptyListMessage = "No hay institutos registrados.",
            retryButton = "Reintentar Carga",
            fabCreate = "Crear Nuevo Instituto",
            backButton = "Volver",
            foundationPrefix = "Fundación:",
            cityPrefix = "Ciudad:",
            languagePrefix = "Idioma:",
            usersLabel = "usuarios",
            coursesLabel = "cursos",
            editAction = "Editar",
            deleteAction = "Eliminar",
            usersAction = "Ver Usuarios",
            coursesAction = "Ver Cursos",
            deleteDialogTitle = "Confirmar Eliminación",
            deleteDialogMessage = { name -> "¿Estás seguro de que quieres eliminar el instituto \"$name\" de forma permanente?" },
            cancelAction = "Cancelar"
        ),
        form = InstituteFormStrings(
            titleRegister = "Registrar Nuevo Instituto",
            titleEdit = { name -> "Editar Instituto: $name" },
            buttonCreate = "Crear Instituto",
            buttonSave = "Guardar Cambios",
            fieldName = "Nombre del Instituto",
            fieldFoundationDate = "Fecha de Fundación (DD/MM/AAAA)",
            fieldCity = "Ciudad",
            fieldLanguage = "Idioma",
            success = "¡Operación exitosa!"
        ),
        feedback = FeedbackMessages(
            requiredName = "El nombre del instituto es obligatorio.",
            invalidName = "El nombre contiene caracteres no permitidos o excede los 50 caracteres.",
            requiredFoundationDate = "La fecha de fundación es obligatoria.",
            incompleteFoundationDate = "Complete la fecha (DD/MM/AAAA).",
            invalidDateFormat = "Formato de fecha inválido. Use DD/MM/AAAA.",
            requiredCity = "Ciudad es obligatorio.",
            requiredLanguage = "El idioma es obligatorio y debe ser válido.",
            formError = "Corrige los errores en el formulario.",
            sessionExpired = "Sesión caducada. Por favor, vuelve a iniciar sesión.",
            unknownError = "Error desconocido.",
            noInstituteSelected = "No se ha seleccionado un instituto para editar.",
            successDelete = "Instituto eliminado exitosamente.",
            successCreate = "Instituto creado exitosamente.",
            successUpdate = "Instituto actualizado exitosamente."
        )
    )

    private val EN = InstitutesContent(
        list = InstituteListStrings(
            titleScreen = "Institute Management",
            emptyListMessage = "No registered institutes found.",
            retryButton = "Retry Load",
            fabCreate = "Create New Institute",
            backButton = "Go Back",
            foundationPrefix = "Foundation:",
            cityPrefix = "City:",
            languagePrefix = "Language:",
            usersLabel = "users",
            coursesLabel = "courses",
            editAction = "Edit",
            deleteAction = "Delete",
            usersAction = "View Users",
            coursesAction = "View Courses",
            deleteDialogTitle = "Confirm Deletion",
            deleteDialogMessage = { name -> "Are you sure you want to permanently delete the institute \"$name\"?" },
            cancelAction = "Cancel"
        ),
        form = InstituteFormStrings(
            titleRegister = "Register New Institute",
            titleEdit = { name -> "Edit Institute: $name" },
            buttonCreate = "Create Institute",
            buttonSave = "Save Changes",
            fieldName = "Institute Name",
            fieldFoundationDate = "Foundation Date (DD/MM/YYYY)",
            fieldCity = "City",
            fieldLanguage = "Language",
            success = "Operation successful!"
        ),
        feedback = FeedbackMessages(
            requiredName = "The institute name is mandatory.",
            invalidName = "Name contains non-allowed characters or exceeds 50 characters.",
            requiredFoundationDate = "The foundation date is mandatory.",
            incompleteFoundationDate = "Complete the date (DD/MM/YYYY).",
            invalidDateFormat = "Invalid date format. Use DD/MM/YYYY.",
            requiredCity = "City is mandatory.",
            requiredLanguage = "Language is mandatory and must be valid.",
            formError = "Please correct the form errors.",
            sessionExpired = "Session expired. Please log in again.",
            unknownError = "Unknown error.",
            noInstituteSelected = "No institute has been selected for editing.",
            successDelete = "Institute deleted successfully.",
            successCreate = "Institute created successfully.",
            successUpdate = "Institute updated successfully."
        )
    )

    fun get(langCode: String): InstitutesContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}