package com.jaco.cc3d.core.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface LoadingHandler {
    val isLoading: StateFlow<Boolean>

    val errorEvents: SharedFlow<String>

    suspend fun <T> runWithLoading(
        loadingState: MutableStateFlow<Boolean>,
        errorFlow: MutableSharedFlow<String>? = null,
        block: suspend () -> T
    ): T? {
        loadingState.value = true
        return try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
            val message = e.localizedMessage ?: "Error desconocido"
            errorFlow?.emit(message)
            null
        } finally {
            loadingState.value = false
        }
    }
}