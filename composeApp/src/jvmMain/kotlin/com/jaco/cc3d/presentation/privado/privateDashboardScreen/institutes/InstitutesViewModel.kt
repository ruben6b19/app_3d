package com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaco.cc3d.domain.models.Institute
import com.jaco.cc3d.domain.models.InstituteDomainRequest
import com.jaco.cc3d.domain.usecases.institute.CreateInstitute
import com.jaco.cc3d.domain.usecases.institute.DeleteInstitute
import com.jaco.cc3d.domain.usecases.institute.GetInstitutes
import com.jaco.cc3d.domain.usecases.institute.UpdateInstitute
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jaco.cc3d.utils.ValidationRegex
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.FeedbackMessages
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.InstitutesResources
import com.jaco.cc3d.utils.convertDdMmYyyyToIso
import com.jaco.cc3d.utils.formatIsoDateToDdMmYyyy
import com.jaco.cc3d.data.network.utils.handleApiFailure

/**
 * Define los posibles modos de la interfaz de usuario.
 */
enum class InstitutesUiMode {
    LIST, CREATE, EDIT
}

class InstitutesViewModel @Inject constructor(
    private val getInstitutesUseCase: GetInstitutes,
    private val createInstituteUseCase: CreateInstitute,
    private val updateInstituteUseCase: UpdateInstitute,
    private val deleteInstituteUseCase: DeleteInstitute,
) : ScreenModel {

    var hasFetched by mutableStateOf(false)
        private set
    var lang by mutableStateOf("en")
     //   private set
    // --- Estado de la Interfaz de Usuario (UI State) ---
    var institutes by mutableStateOf<List<Institute>>(emptyList())
        private set
    var uiMode by mutableStateOf(InstitutesUiMode.LIST)
        private set
    var selectedInstitute by mutableStateOf<Institute?>(null)
        private set

    // --- Estado de Carga y Feedback ---
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null) // 💡 NUEVO: Clave de mensaje de éxito para Snackbar
        private set
    //var isSuccess by mutableStateOf(false)
    //    private set
    var mustLogout by mutableStateOf(false)
        private set

    // --- Estado de los Campos del Formulario ---
    var name by mutableStateOf("")
        private set
    var foundationDate by mutableStateOf("")
        private set
    var cityCodeInput by mutableStateOf("") // Se maneja como String para la entrada
        private set

    var language by mutableStateOf("")
        private set

    // 💡 ESTADOS DE ERROR POR CAMPO: CAMBIADOS A STRING?
    var nameError by mutableStateOf<String?>(null)
        private set
    var foundationDateError by mutableStateOf<String?>(null) // CORREGIDO: Ahora es String?
        private set
    var cityCodeInputError by mutableStateOf<String>("") // CORREGIDO: Ahora es String?
        private set
    var languageError by mutableStateOf<String?>(null)
        private set


    init {
        fetchInstitutes()
    }

    private fun getFeedbackMessages(): FeedbackMessages {
        //return FeedbackTexts.getMessages(lang)
        return InstitutesResources.get(lang).feedback
    }

    // --- Manejo del Estado de la Interfaz ---
    fun enterCreateMode() {
        resetForm()
        uiMode = InstitutesUiMode.CREATE
    }

    fun enterEditMode(institute: Institute) {
        selectedInstitute = institute
        name = institute.name
        val formattedDate = formatIsoDateToDdMmYyyy(institute.foundationDate)
        foundationDate = formattedDate
        //foundationDate = TextFieldValue(institute.foundationDate)// Asumiendo formato YYYY-MM-DD
        cityCodeInput = institute.city.toString()
        language = institute.language
        uiMode = InstitutesUiMode.EDIT
    }

    fun exitFormMode() {
        resetForm()
        uiMode = InstitutesUiMode.LIST
    }

    private fun resetForm() {
        name = ""
        foundationDate = ""
        cityCodeInput = ""
        language = ""
        resetFeedback()
        // Los errores de campo se resetean en resetFeedback()
    }

    fun onLogoutHandled() {
        mustLogout = false
    }

    fun clearSuccessMessage() {
        successMessage = null
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
    /*private fun handleFailure(exception: Throwable): String {
        val messages = getFeedbackMessages()
        // Obtenemos la representación completa del error como texto
        val errorString = exception.toString()
        val errorMessage = exception.message ?: ""

        // 1. Definimos la "huella digital" del error de sesión
        // Buscamos el nombre de tu excepción O el mensaje que pusiste en el Interceptor
        val isSessionError = errorString.contains("SessionExpiredException") ||
                errorString.contains("Token refresh failed") ||
                errorMessage.contains("Token refresh failed")

        if (isSessionError) {
            mustLogout = true // Activa la navegación
            return messages.sessionExpired
        }

        // Retorno genérico
        return exception.message ?: messages.unknownError
    }*/

    private fun resetFeedback() {
        isLoading = false
        //isSuccess = false
        successMessage = null
        errorMessage = null
        resetFieldErrors()
    }

    // 💡 FUNCIÓN PARA RESETEAR ERRORES DE CAMPO
    private fun resetFieldErrors() {
        nameError = null
        foundationDateError = null // CORREGIDO: Ahora es null
        cityCodeInputError = "" // CORREGIDO: Ahora es null
        languageError = null
    }

    // --- Manejo de Cambios de Input (con limpieza de error) ---
    fun onNameChange(newValue: String) {
        name = newValue
        // 💡 AL TECLEAR: Validamos para dar feedback instantáneo
        validateInput()
    }

    fun onFoundationDateChange(newValue: String) {
        foundationDate = newValue
        validateInput()
    }

    fun onCityCodeChange(newValue: String) {
        cityCodeInput = newValue
        // 💡 AL TECLEAR: Validamos para dar feedback instantáneo
        validateInput()
    }

    fun onLanguageChange(newValue: String) {
        language = newValue
        // 💡 AL TECLEAR: Validamos para dar feedback instantáneo
        validateInput()
    }
    fun clearErrorMessage() {
        errorMessage = null
    }
    // --- OPERACIONES CRUD ---

    /**
     * READ ALL: Carga la lista de institutos.
     */
    fun fetchInstitutes() {
        screenModelScope.launch {
            resetFeedback()
            isLoading = true
            getInstitutesUseCase().onSuccess {
                institutes = it
                errorMessage = null // Limpiar mensaje de error si la carga es exitosa
            }.onFailure { exception ->
                errorMessage = handleFailure(exception)
            }
            isLoading = false
            hasFetched = true
        }
    }

    /**
     * CREATE: Crea un nuevo instituto.
     */
    fun createInstitute() {
        if (!validateInput()) return // 💡 Guard Clause: Detener si la validación falla

        screenModelScope.launch {
            resetFeedback()
            isLoading = true;
            val isoFoundationDate = try {
                convertDdMmYyyyToIso(foundationDate)
            } catch (e: Exception) {
                errorMessage = getFeedbackMessages().invalidDateFormat // Mensaje de error si la conversión falla (debería ser capturado por validateInput)
                isLoading = false
                return@launch
            }

            val request = InstituteDomainRequest(
                name = name,
                foundationDate = isoFoundationDate,
                city = cityCodeInput.toInt(), // cityCodeInput ya es validado como Int
                language = language

            )

            createInstituteUseCase(request).onSuccess {
                institutes = listOf(it) + institutes
               // isSuccess = true
                exitFormMode() // Volver a la lista al terminar
            }.onFailure { exception ->
                //errorMessage = exception.message ?: "Error al crear el instituto."
                errorMessage = handleFailure(exception)
            }
            isLoading = false
        }
    }

    /**
     * UPDATE: Actualiza un instituto existente.
     */
    fun updateInstitute() {
        if (!validateInput()) return // 💡 Guard Clause: Detener si la validación falla

        val messages = getFeedbackMessages()

        val instituteToUpdate = selectedInstitute
        if (instituteToUpdate == null) {
            errorMessage = messages.noInstituteSelected
            return
        }

        screenModelScope.launch {
            resetFeedback()
            isLoading = true;
            val isoFoundationDate = try {
                convertDdMmYyyyToIso(foundationDate)
            } catch (e: Exception) {
                errorMessage = getFeedbackMessages().invalidDateFormat
                isLoading = false
                return@launch
            }

            val request = InstituteDomainRequest(
                name = name,
                foundationDate = isoFoundationDate,
                city = cityCodeInput.toInt(),
                language = language
            )

            updateInstituteUseCase(instituteToUpdate.id, request).onSuccess {
                //isSuccess = true
                institutes = institutes.map { current ->
                    if (current.id == it.id) it else current
                }
                //fetchInstitutes() // Refrescar la lista
                exitFormMode() // Volver a la lista al terminar
            }.onFailure { exception ->
                //errorMessage = exception.message ?: "Error al actualizar el instituto."
                errorMessage = handleFailure(exception)
            }
            isLoading = false
        }
    }

    /**
     * DELETE: Elimina un instituto por su ID.
     */
    fun deleteInstitute(instituteId: String) {
        screenModelScope.launch {
            resetFeedback()
            isLoading = true
            deleteInstituteUseCase(instituteId).onSuccess {
                println("Eliminando ID: '$instituteId'")

                successMessage = "success_institute_deleted"
                // 2. Filtra y asigna
                val listaFiltrada = institutes.filter {
                    // Log para ver qué estamos comparando
                    val sonIguales = it.id == instituteId // Asumiendo que ambos son String
                    if(sonIguales) println("¡Encontrado coincidencia para eliminar!: ${it.name}")
                    !sonIguales
                }

                // 3. Verifica si el tamaño cambió
                println("Tamaño antes: ${institutes.size} - Tamaño después: ${listaFiltrada.size}")

                institutes = listaFiltrada
                //fetchInstitutes() // Refrescar la lista
            }.onFailure { exception ->
                //errorMessage = exception.message ?: "Error al eliminar el instituto."
                errorMessage = handleFailure(exception)
            }
            isLoading = false
        }
    }

    // --- VALIDACIÓN INTERNA (MEJORADA) ---

    // Regex simple para YYYY-MM-DD
    //private val dateRegex = "^\\d{4}-\\d{2}-\\d{2}\$".toRegex()
    private val dateRegex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$".toRegex()
    // val allowedCharsRegex = remember { Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$") }

    /**
     * Valida todos los campos del formulario y asigna mensajes de error específicos.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    fun validateInput(): Boolean { // Cambiado a 'fun' pública para que la UI la pueda llamar
        resetFieldErrors() // Limpiar errores antes de validar
        var isValid = true
        val dateInputIsComplete = foundationDate.length == 10

        val messages = getFeedbackMessages() // 👈 USAMOS LA FUNCIÓN AQUÍ

        // 2. Validar Nombre
        if (name.isBlank()) {
            nameError = messages.requiredName // 👈 CAMBIO
            isValid = false
        } else if (!ValidationRegex.VALIDATE_TEXT.matches(name)) {
            nameError = messages.invalidName // 👈 CAMBIO
            isValid = false
        }

        // 3. Validar Fecha de Fundación (DD/MM/AAAA)
        if (foundationDate.isBlank()) {
            foundationDateError = messages.requiredFoundationDate // 👈 CAMBIO
            isValid = false
        } else if (!dateInputIsComplete) {
            foundationDateError = messages.incompleteFoundationDate // 👈 CAMBIO
            isValid = false
        } else if (!foundationDate.matches(dateRegex)) {
            foundationDateError = messages.invalidDateFormat // 👈 CAMBIO
            isValid = false
        } else {
            foundationDateError = null
        }

        // 4. Validar Código de Ciudad (Numérico)
        if (cityCodeInput.isBlank()) {
            cityCodeInputError = messages.requiredCity // 👈 CAMBIO
            isValid = false
        } else {
            cityCodeInputError = ""
        }

        // 5. Validar Idioma
        if (language.isBlank() || language.length < 2) {
            languageError = messages.requiredLanguage // 👈 CAMBIO
            isValid = false
        } else {
            languageError = null
        }

        // Si la validación falla, podemos poner un mensaje genérico de formulario.
        if (!isValid) {
            errorMessage = messages.formError // 👈 CAMBIO
        } else {
            errorMessage = null
        }

        return isValid
    }
}