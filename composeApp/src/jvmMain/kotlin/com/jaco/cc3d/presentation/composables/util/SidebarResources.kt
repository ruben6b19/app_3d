package com.jaco.cc3d.presentation.composables.util

data class SidebarStrings(
    val title: String,
    val closeDescription: String,
    val screenModeTitle: String,
    val darkMode: String,
    val lightMode: String,
    val languageTitle: String,
    val languageLabel: String,
    val selectLanguage: String
)

object SidebarResources {
    private val en = SidebarStrings(
        title = "Settings",
        closeDescription = "Close Panel",
        screenModeTitle = "Screen Mode:",
        darkMode = "Dark Mode",
        lightMode = "Light Mode",
        languageTitle = "UI Language:",
        languageLabel = "Language",
        selectLanguage = "Select"
    )

    private val es = SidebarStrings(
        title = "Configuración",
        closeDescription = "Cerrar Panel",
        screenModeTitle = "Modo de Pantalla:",
        darkMode = "Modo Oscuro",
        lightMode = "Modo Claro",
        languageTitle = "Idioma de la UI:",
        languageLabel = "Idioma",
        selectLanguage = "Seleccionar"
    )

    fun get(lang: String): SidebarStrings {
        return when (lang) {
            "en" -> en
            else -> es
        }
    }
}