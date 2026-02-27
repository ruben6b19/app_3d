package com.jaco.cc3d.presentation.publico.login


import cafe.adriel.voyager.core.model.ScreenModel
//import cafe.adriel.voyager.core.model
//import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import cafe.adriel.voyager.core.model.screenModelScope
import com.jaco.cc3d.data.local.preferences.EncryptedDesktopTokenManager
import com.jaco.cc3d.data.network.apiAuth.UserData
import com.jaco.cc3d.domain.usecases.auth.LoginUseCase
//import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject // Usaremos @Inject para la inyección (si no usas Hilt)

// Estado de la UI
data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)

// 🔑 CLASE DEL VIEWMODEL (ScreenModel en Voyager)
class LoginViewModel @Inject constructor(
    //private val authRepository: AuthRepository // Inyectamos el Repositorio (Dagger)
    private val loginUseCase: LoginUseCase,
    private val tokenManager: EncryptedDesktopTokenManager
) : ScreenModel {

    // 🔑 Estado mutable y visible para la UI
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    init {
        // Comprueba el estado de login al iniciar el ViewModel
        checkInitialLoginState()
    }

    fun getUserData(): UserData? {
        return tokenManager.getUserData()
    }
    fun getLastRole(): Int? = tokenManager.getLastRole()

    fun switchRole(newRole: Int) {
        //tokenManager.saveLastRole(newRole)
        // Opcional: Podrías disparar un evento de navegación aquí si fuera necesario
        println("Rol cambiado a: $newRole")
    }

    fun saveLastRole(roleId: Int) {
        tokenManager.saveLastRole(roleId)
    }
    /**
     * Revisa si el usuario puede saltar el login según el estado de la sesión y red.
     */
    private fun checkInitialLoginState() {
        val user = tokenManager.getUserData()
        val canAccess = tokenManager.canAccessDashboard()
        val isOnline = tokenManager.isOnline()
        val isExpired = !tokenManager.isSessionValid()

        if (canAccess) {
            // Permitimos la navegación (LoginScreen detectará isSuccess y navegará)
            _state.value = _state.value.copy(
                isLoading = false,
                error = null,
                isSuccess = true
            )
        } else {
            // Si no puede entrar y estamos ONLINE con token vencido, limpiamos por seguridad
            if (isOnline && isExpired && tokenManager.getAccessToken() != null) {
                tokenManager.clearTokens()
                _state.value = _state.value.copy(
                    error = "Su sesión ha expirado. Conéctese a internet para renovarla."
                )
            }
        }
    }
    /**
     * Revisa si existen tokens de acceso y refresco para saltar el login.
     */
    private fun checkInitialLoginState3() {
        val user = tokenManager.getUserData()
        // 🎯 Usamos la lógica encapsulada en el manager
        val hasValidSession = tokenManager.isSessionValid()

        // Solo marcamos éxito si los datos del usuario existen Y la sesión es válida por tiempo
        if (user != null && hasValidSession) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = null,
                isSuccess = true // 👈 Dispara la navegación automática en LoginScreen
            )
        } else {
            // 💡 Si hay un token pero la sesión NO es válida (expiró),
            // limpiamos los datos para obligar a un nuevo login.
            if (tokenManager.getAccessToken() != null && !hasValidSession) {
                tokenManager.clearTokens()
                _state.value = _state.value.copy(
                    error = "Su sesión ha expirado por seguridad. Inicie sesión nuevamente."
                )
            }
        }
    }
    private fun checkInitialLoginState2() {
        val accessToken = tokenManager.getAccessToken()
        val user = tokenManager.getUserData()

        // Solo marcamos éxito si tenemos el token Y los datos del usuario
        if (!accessToken.isNullOrEmpty() && user != null) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = null, // Confirmamos que el error es nulo
                isSuccess= true,
            )
        } else {
            // Si falta algo, aseguramos limpieza, pero NO cuando todo está bien
            // tokenManager.clearTokens()
        }
    }

    fun login(email: String, password: String) {
        // Usamos coroutineScope de ScreenModel para iniciar la corrutina
        screenModelScope.launch {

            _state.value = _state.value.copy(isLoading = true, error = null)
            loginUseCase(email, password)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message // Mostramos el mensaje de error propagado
                    )
                }
        }
    }
}