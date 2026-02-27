// com/jaco/cc3d/presentation/privado/users/UsersViewModel.kt

package com.jaco.cc3d.presentation.privado.privateDashboardScreen.users

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.domain.models.UserDomainRequest
import com.jaco.cc3d.domain.usecases.user.CreateUser
import com.jaco.cc3d.domain.usecases.user.DeleteUser
import com.jaco.cc3d.domain.usecases.user.GetUsers
import com.jaco.cc3d.domain.usecases.user.UpdateUser
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.util.UsersResources
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.util.UserFeedbackMessages
import com.jaco.cc3d.utils.ValidationRegex
import com.jaco.cc3d.data.network.utils.handleApiFailure
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UsersUiMode {
    LIST, CREATE, EDIT
}

class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsers,
    private val createUserUseCase: CreateUser,
    private val updateUserUseCase: UpdateUser,
    private val deleteUserUseCase: DeleteUser,
) : ScreenModel {

    var hasFetched by mutableStateOf(false)
        private set
    // --- Configuración (Sync con UI) ---
    var lang by mutableStateOf("en")
    var selectedInstituteId by mutableStateOf<String?>(null) // ID del instituto para filtrar

    // --- Estado de la Interfaz de Usuario (UI State) ---
    var users by mutableStateOf<List<User>>(emptyList())
        private set
    var uiMode by mutableStateOf(UsersUiMode.LIST)
        private set
    var selectedUser by mutableStateOf<User?>(null)
        private set

    // --- Estado de Carga y Feedback ---
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess by mutableStateOf(false)
        private set
    var mustLogout by mutableStateOf(false)
        private set

    // --- Estado de los Campos del Formulario ---
    var email by mutableStateOf("")
        private set
    var fullName by mutableStateOf("")
        private set
    var password by mutableStateOf("") // 💡 Campo de Contraseña
        private set
    var roleInput by mutableStateOf<List<Int>>(listOf(0))
        private set
    var statusInput by mutableStateOf(1) // 1: active

    // 💡 ESTADOS DE ERROR POR CAMPO
    var emailError by mutableStateOf<String?>(null)
        private set
    var fullNameError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null) // 💡 Estado de Error de Contraseña
        private set
    var roleError by mutableStateOf<String?>(null)
        private set
    var statusError by mutableStateOf<String?>(null)
        private set

    // Función para actualizar el ID del instituto (llamada típicamente desde el Composable padre)
    fun setInstituteId(id: String) {
        if (selectedInstituteId != id) {
            selectedInstituteId = id
            hasFetched = false
            fetchUsers()
        }
    }

    private fun getFeedbackMessages(): UserFeedbackMessages {
        return UsersResources.get(lang).feedback
    }

    private fun handleFailure(exception: Throwable): String {
        val messages = getFeedbackMessages()

        return handleApiFailure(
            exception = exception,
            sessionExpiredMessage = messages.sessionExpired, // Asume que UserFeedbackMessages tiene 'sessionExpired'
            unknownErrorMessage = messages.unknownError,     // Asume que UserFeedbackMessages tiene 'unknownError'
            onSessionExpired = { mustLogout = true }
        )
    }
    // --- Implementaciones de UI Mode, Handlers, Reset y CRUD (similares a InstitutesViewModel) ---

    fun enterCreateMode() { resetForm(); uiMode = UsersUiMode.CREATE }
    fun enterEditMode(user: User) {
        selectedUser = user; email = user.email; fullName = user.fullName;
        password=""
        roleInput = user.role; statusInput = user.status; uiMode = UsersUiMode.EDIT
    }
    fun exitFormMode() { resetForm(); uiMode = UsersUiMode.LIST }
    private fun resetForm() {
        email = ""; fullName = ""; password=""; roleInput = listOf(0); statusInput = 1; resetFeedback()
    }

    fun onLogoutHandled() { mustLogout = false }

    fun clearErrorMessage() {
        errorMessage = null
    }

    private fun resetFeedback() { isLoading = false; isSuccess = false; errorMessage = null; resetFieldErrors() }
    private fun resetFieldErrors() { emailError = null; fullNameError = null; roleError = null; statusError = null }

    // --- Manejo de Cambios de Input ---
    fun onEmailChange(newValue: String) { email = newValue; validateInput() }
    fun onFullNameChange(newValue: String) { fullName = newValue; validateInput() }
    fun onPasswordChange(newValue: String) { password = newValue; validateInput() }
    fun onRoleChange(newRole: Int) {
        if (roleInput.contains(newRole)) { roleInput = roleInput.filter { it != newRole } } else { roleInput = roleInput + newRole }
        validateInput()
    }
    fun onStatusChange(newStatus: Int) { statusInput = newStatus; validateInput() }


    // --- OPERACIONES CRUD ---

    fun fetchUsers() {
        val instituteId = selectedInstituteId
        val messages = getFeedbackMessages()
        if (instituteId.isNullOrBlank()) { errorMessage = messages.noInstituteSelected; return }

        screenModelScope.launch {
            resetFeedback()
            isLoading = true;
            getUsersUseCase(instituteId = instituteId).onSuccess {
                users = it
                errorMessage = null
            }.onFailure { exception -> errorMessage = handleFailure(exception) }
            isLoading = false
            hasFetched = true
        }
    }

    fun createUser() {
        if (!validateInput()) return
        val instituteId = selectedInstituteId; val messages = getFeedbackMessages()
        if (instituteId.isNullOrBlank()) { errorMessage = messages.noInstituteSelected; return }

        screenModelScope.launch {
            resetFeedback()
            isLoading = true;
            val request = UserDomainRequest(email = email, fullName = fullName, role = roleInput, instituteId = instituteId, password = password)
            createUserUseCase(request).onSuccess {
                users = listOf(it) + users; isSuccess = true; exitFormMode()
            }.onFailure { exception -> errorMessage = handleFailure(exception) }
            isLoading = false
        }
    }

    fun updateUser() {
        if (!validateInput()) return
        val messages = getFeedbackMessages()
        val userToUpdate = selectedUser
        val instituteId = selectedInstituteId

        if (userToUpdate == null) { errorMessage = messages.noUserSelected; return }
        if (instituteId.isNullOrBlank()) { errorMessage = messages.noInstituteSelected; return }

        screenModelScope.launch {
            resetFeedback()
            isLoading = true

            // 🎯 LÓGICA CLAVE:
            // Si el campo 'password' está vacío o solo tiene espacios,
            // enviamos null para que el caso de uso/backend no lo actualice.
            val passwordToUpdate = if (password.isNotBlank()) password else ""

            val request = UserDomainRequest(
                email = email,
                fullName = fullName,
                role = roleInput,
                instituteId = instituteId,
                password = passwordToUpdate // Enviamos el valor procesado
            )

            updateUserUseCase(userToUpdate.id, request).onSuccess { updatedUser ->
                isSuccess = true
                users = users.map { current -> if (current.id == updatedUser.id) updatedUser else current }
                exitFormMode()
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isLoading = false
        }
    }
    fun updateUser2() {
        if (!validateInput()) return
        val messages = getFeedbackMessages(); val userToUpdate = selectedUser; val instituteId = selectedInstituteId
        if (userToUpdate == null) { errorMessage = messages.noUserSelected; return }
        if (instituteId.isNullOrBlank()) { errorMessage = messages.noInstituteSelected; return }

        screenModelScope.launch {
            resetFeedback()
            isLoading = true;
            val request = UserDomainRequest(email = email, fullName = fullName, role = roleInput, instituteId = instituteId, password = password)
            updateUserUseCase(userToUpdate.id, request).onSuccess {
                isSuccess = true; users = users.map { current -> if (current.id == it.id) it else current }; exitFormMode()
            }.onFailure { exception -> errorMessage = handleFailure(exception) }
            isLoading = false
        }
    }

    fun deleteUser(userId: String) {
        screenModelScope.launch {
            resetFeedback();
            isLoading = true;
            deleteUserUseCase(userId).onSuccess {
                users = users.filter { it.id != userId }
            }.onFailure {
                exception ->
                errorMessage = handleFailure(exception)
            }
            isLoading = false
        }
    }

    // --- VALIDACIÓN INTERNA ---
    fun validateInput(): Boolean {
        resetFieldErrors(); var isValid = true; val messages = getFeedbackMessages()

        if (email.isBlank()) { emailError = messages.requiredEmail; isValid = false }
        else if (!ValidationRegex.VALIDATE_EMAIL.matches(email)) { emailError = messages.invalidEmail; isValid = false }
        else { emailError = null }

        if (fullName.isBlank()) { fullNameError = messages.requiredFullName; isValid = false }
        else if (!ValidationRegex.VALIDATE_TEXT.matches(fullName)) { fullNameError = messages.invalidFullName; isValid = false }
        else { fullNameError = null }

        if (uiMode == UsersUiMode.CREATE) {
            if (password.isBlank()) { passwordError = messages.requiredPassword; isValid = false } // Asume que messages.requiredPassword existe
            // Puedes añadir una validación de longitud/complejidad aquí:
            else if (password.length < 6) { passwordError = messages.passwordTooShort; isValid = false } // Asume que messages.passwordTooShort existe
            else { passwordError = null }
        } else {
            passwordError = null
        }

        if (roleInput.isEmpty()) { roleError = messages.requiredRole; isValid = false }
        else { roleError = null }

        statusError = null // Dejamos la validación de estado simple

        if (!isValid) { errorMessage = messages.formError } else { errorMessage = null }
        return isValid
    }
}