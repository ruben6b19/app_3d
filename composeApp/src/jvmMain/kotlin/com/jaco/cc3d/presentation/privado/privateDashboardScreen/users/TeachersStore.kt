package com.jaco.cc3d.presentation.privado.privateDashboardScreen.users

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaco.cc3d.domain.models.User // 💡 Usamos el modelo User
import com.jaco.cc3d.domain.usecases.user.GetUsers // 💡 Use Case para obtener usuarios
import kotlinx.coroutines.launch
import javax.inject.Inject

// Definimos la constante del rol (Asumiendo que 1 es el rol de Teacher)
private const val TEACHER_ROLE = 1

class TeachersStore @Inject constructor(
    private val getUsersUseCase: GetUsers,
) : ScreenModel {

    // Lista ya filtrada de profesores.
    // 💡 Quitamos 'private set' para permitir actualizaciones CRUD desde el ViewModel.
    var teachers by mutableStateOf<List<User>>(emptyList())

    var isLoading by mutableStateOf(false)
        private set
    var hasLoaded by mutableStateOf(false)
        private set

    // 1. NUEVO: Estado de Error
    var loadError by mutableStateOf<String?>(null)
        private set

    // Guardamos el ID actual para poder reintentar sin pedirlo de nuevo
    private var currentInstituteId: String? = null

    /**
     * Carga los usuarios para el instituto dado y filtra por el rol de profesor.
     */
    fun loadTeachersIfNecessary(instituteId: String) {
        // Si ya cargó y el ID es el mismo, no hacemos nada
        if (hasLoaded && currentInstituteId == instituteId) return
        if (isLoading || instituteId.isBlank()) return

        currentInstituteId = instituteId
        performLoad(instituteId)
    }

    // 2. NUEVO: Función pública para forzar reintento
    fun retryLoad() {
        // Usamos el ID guardado. Si es nulo, no podemos reintentar.
        val instituteId = currentInstituteId ?: return

        if (isLoading) return
        performLoad(instituteId)
    }

    // Lógica privada de carga para no repetir código
    private fun performLoad(instituteId: String) {
        screenModelScope.launch {
            isLoading = true
            loadError = null // Limpiar error previo

            getUsersUseCase(instituteId).onSuccess { users ->
                // Filtrado por rol
                teachers = users.filter { it.role.contains(TEACHER_ROLE) }
                hasLoaded = true
            }.onFailure { exception ->
                // 3. Capturar el error
                loadError = exception.message ?: "Error al cargar profesores"
                hasLoaded = false
            }
            isLoading = false
        }
    }
}