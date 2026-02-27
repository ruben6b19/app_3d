package com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay

// Archivo: BaseViewModel.kt (Opcional, para estructurar mejor)
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

interface BaseViewModel {
    // Definimos un CoroutineScope que usa un SupervisorJob
    // para que los fallos de un Job no cancelen otros,
    // y Dispatchers.Default (o .IO si es necesario)
    val viewModelScope: CoroutineScope
        get() = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Función para cancelar el Scope cuando el ViewModel se 'destruya'
    fun onCleared() {
        viewModelScope.cancel()
    }
}