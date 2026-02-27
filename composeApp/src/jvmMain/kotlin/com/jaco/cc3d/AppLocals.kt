package com.jaco.cc3d

import androidx.compose.runtime.compositionLocalOf

// --- 1. ACCIONES DE LA BARRA LATERAL ---
interface SidebarActions {
    val isOpen: Boolean
    fun toggleSidebar()
    fun registerFocusRecovery(action: () -> Unit)
}

/**
 * CompositionLocal que permite acceder a la implementación de SidebarActions.
 * Proporcionado a nivel superior de la aplicación (en main.kt).
 */
val LocalSidebarActions = compositionLocalOf<SidebarActions> { error("No SidebarActions provided") }

// --- 2. ACCIONES DEL TEMA ---
interface ThemeActions {
    val isDark: Boolean
    fun toggleTheme()
}

// Provee un valor por defecto que no hace nada, para evitar nulos.
val LocalThemeActions = compositionLocalOf<ThemeActions> {
    error("No ThemeActions provided")
}
// ===========================================
// 3. ACCIONES DEL LENGUAJE (Language)
// ===========================================
interface LanguageActions {
    /**
     * El código de idioma actualmente seleccionado (ej: "es", "en").
     */
    val currentLanguage: String

    /**
     * Función para cambiar el idioma de la aplicación.
     * @param languageCode El nuevo código de idioma a establecer.
     */
    fun setLanguage(languageCode: String)
}

/**
 * CompositionLocal que permite a cualquier componente acceder al estado
 * y las funciones de cambio de idioma global.
 */
val LocalLanguageActions = compositionLocalOf<LanguageActions> {
    error("No LanguageActions provided")
}