// com/jaco/cc3d/presentation/privado/users/util/UsersStrings.kt

package com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.util

import java.util.Locale

// Data Classes (similares a InstitutesContent)
data class UsersContent(
    val list: UserListStrings,
    val form: UserFormStrings,
    val feedback: UserFeedbackMessages
)

data class UserListStrings(
    val titleScreen: String,
    val emptyListMessage: String,
    val retryButton: String,
    val fabCreate: String,
    val backButton: String,
    val rolePrefix: String,
    val statusPrefix: String,
    val institutePrefix: String,
    val editAction: String,
    val deleteAction: String,
    val deleteDialogTitle: String,
    val deleteDialogMessage: (String) -> String,
    val cancelAction: String
)

data class UserFormStrings(
    val titleRegister: String,
    val titleEdit: (String) -> String,
    val buttonCreate: String,
    val buttonSave: String,
    val fieldEmail: String,
    val fieldFullName: String,
    val fieldPassword: String,
    val fieldRole: String,
    val fieldStatus: String,
    val success: String
)

data class UserFeedbackMessages(
    val requiredEmail: String,
    val invalidEmail: String,
    val requiredFullName: String,
    val invalidFullName: String,
    val requiredPassword: String, // 💡 Nuevo mensaje de error: Contraseña obligatoria
    val passwordTooShort: String,
    val requiredRole: String,
    val requiredStatus: String,
    val formError: String,
    val sessionExpired: String,
    val unknownError: String,
    val noUserSelected: String,
    val noInstituteSelected: String // Nuevo mensaje específico
)

// Objeto Singleton con las traducciones
object UsersResources {

    private val ES = UsersContent(
        list = UserListStrings(
            titleScreen = "Gestión de Usuarios",
            emptyListMessage = "No hay usuarios registrados en este instituto.",
            retryButton = "Reintentar Carga",
            fabCreate = "Crear Nuevo Usuario",
            backButton = "Volver",
            rolePrefix = "Rol:",
            statusPrefix = "Estado:",
            institutePrefix = "Instituto:",
            editAction = "Editar",
            deleteAction = "Eliminar",
            deleteDialogTitle = "Confirmar Eliminación",
            deleteDialogMessage = { name -> "¿Estás seguro de que quieres eliminar al usuario \"$name\" de forma permanente?" },
            cancelAction = "Cancelar"
        ),
        form = UserFormStrings(
            titleRegister = "Registrar Nuevo Usuario",
            titleEdit = { name -> "Editar Usuario: $name" },
            buttonCreate = "Crear Usuario",
            buttonSave = "Guardar Cambios",
            fieldEmail = "Correo Electrónico",
            fieldFullName = "Nombre Completo",
            fieldPassword = "Contraseña",
            fieldRole = "Rol",
            fieldStatus = "Estado",
            success = "¡Operación exitosa!"
        ),
        feedback = UserFeedbackMessages(
            requiredEmail = "El correo electrónico es obligatorio.",
            invalidEmail = "Formato de correo electrónico inválido.",
            requiredFullName = "El nombre completo es obligatorio.",
            invalidFullName = "Nombre contiene caracteres no permitidos o es muy largo.",
            requiredPassword = "La contraseña es obligatoria.", // 💡 Traducción ES
            passwordTooShort = "La contraseña debe tener al menos 6 caracteres.",
            requiredRole = "El rol es obligatorio.",
            requiredStatus = "El estado es obligatorio.",
            formError = "Corrige los errores en el formulario.",
            sessionExpired = "Sesión caducada. Por favor, vuelve a iniciar sesión.",
            unknownError = "Error desconocido.",
            noUserSelected = "No se ha seleccionado un usuario para editar.",
            noInstituteSelected = "No hay un instituto seleccionado para gestionar usuarios."
        )
    )

    private val EN = UsersContent(
        list = UserListStrings(
            titleScreen = "User Management",
            emptyListMessage = "No registered users found in this institute.",
            retryButton = "Retry Load",
            fabCreate = "Create New User",
            backButton = "Go Back",
            rolePrefix = "Role:",
            statusPrefix = "Status:",
            institutePrefix = "Institute:",
            editAction = "Edit",
            deleteAction = "Delete",
            deleteDialogTitle = "Confirm Deletion",
            deleteDialogMessage = { name -> "Are you sure you want to permanently delete the user \"$name\"?" },
            cancelAction = "Cancel"
        ),
        form = UserFormStrings(
            titleRegister = "Register New User",
            titleEdit = { name -> "Edit User: $name" },
            buttonCreate = "Create User",
            buttonSave = "Save Changes",
            fieldEmail = "Email Address",
            fieldFullName = "Full Name",
            fieldPassword = "Password",
            fieldRole = "Role",
            fieldStatus = "Status",
            success = "Operation successful!"
        ),
        feedback = UserFeedbackMessages(
            requiredEmail = "Email address is mandatory.",
            invalidEmail = "Invalid email format.",
            requiredFullName = "Full name is mandatory.",
            invalidFullName = "Name contains non-allowed characters or is too long.",
            requiredPassword = "Password is mandatory.", // 💡 Traducción EN
            passwordTooShort = "Password must be at least 6 characters long.",
            requiredRole = "Role is mandatory.",
            requiredStatus = "Status is mandatory.",
            formError = "Please correct the form errors.",
            sessionExpired = "Session expired. Please log in again.",
            unknownError = "Unknown error.",
            noUserSelected = "No user has been selected for editing.",
            noInstituteSelected = "No institute selected to manage users."
        )
    )

    fun get(langCode: String): UsersContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}