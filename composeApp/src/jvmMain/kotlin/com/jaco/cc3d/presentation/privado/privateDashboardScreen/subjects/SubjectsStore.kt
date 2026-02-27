package com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jaco.cc3d.domain.models.Subject
import com.jaco.cc3d.domain.usecases.subject.GetSubjects
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubjectsStore @Inject constructor(
    private val getSubjectsUseCase: GetSubjects,
) : ScreenModel {

    var subjects by mutableStateOf<List<Subject>>(emptyList())
    // 💡 Quitamos 'private set' para permitir actualizaciones desde ViewModels (como createSubject)
    // O mejor: crear una función 'updateSubjects(newList)' para encapsularlo.

    var isLoading by mutableStateOf(false)
        private set
    var hasLoaded by mutableStateOf(false)
        private set

    // 1. NUEVO: Estado de Error
    var loadError by mutableStateOf<String?>(null)
        private set

    fun loadSubjectsIfNecessary() {
        if (hasLoaded || isLoading) return
        performLoad()
    }

    // 2. NUEVO: Función pública para forzar reintento
    fun retryLoad() {
        if (isLoading) return
        performLoad()
    }

    private fun performLoad() {
        screenModelScope.launch {
            isLoading = true
            loadError = null // Limpiar error previo

            getSubjectsUseCase().onSuccess {
                subjects = it
                hasLoaded = true
            }.onFailure { exception ->
                // 3. Capturar el error
                loadError = exception.message ?: "Error al cargar materias"
                hasLoaded = false
            }
            isLoading = false
        }
    }
}