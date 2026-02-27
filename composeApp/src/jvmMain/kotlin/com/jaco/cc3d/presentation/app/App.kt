package com.jaco.cc3d.presentation.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.jaco.cc3d.LanguageActions
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.LocalSidebarActions
import com.jaco.cc3d.LocalThemeActions
import com.jaco.cc3d.SidebarActions
import com.jaco.cc3d.ThemeActions
import com.jaco.cc3d.core.navigation.Login
import com.jaco.cc3d.domain.session.LanguageConfig
import com.jaco.cc3d.presentation.app.theme.DriverAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    // 1. Estados
    var isDarkTheme by remember { mutableStateOf(true) }
    var isSidebarOpen by remember { mutableStateOf(false) }
    var currentLanguageCode by remember { mutableStateOf("es") }

    var recoverActiveScreenFocus by remember { mutableStateOf<(() -> Unit)?>(null) }
    // 2. SINCRONIZACIÓN INICIAL:
    // Asegura que el Singleton del dominio tenga el valor inicial correcto al arrancar la app.
    LaunchedEffect(Unit) {
        LanguageConfig.currentCode = currentLanguageCode
    }

    LaunchedEffect(isSidebarOpen) {
        if (!isSidebarOpen) {
            recoverActiveScreenFocus?.invoke()
        }
    }

    val themeController = remember {
        object : ThemeActions {
            override val isDark: Boolean get() = isDarkTheme
            override fun toggleTheme() { isDarkTheme = !isDarkTheme }
        }
    }

    val sidebarController = remember {
        object : SidebarActions {
            override val isOpen: Boolean get() = isSidebarOpen
            override fun toggleSidebar() { isSidebarOpen = !isSidebarOpen }
            override fun registerFocusRecovery(action: () -> Unit) {
                recoverActiveScreenFocus = action
            }
        }
    }

    //val languageController = remember {
    //    object : LanguageActions {
    //        override val currentLanguage: String get() = currentLanguageCode

    //        override fun setLanguage(languageCode: String) {
    //            currentLanguageCode = languageCode
    //        }
    //    }
    //}
    // 3. INTEGRACIÓN EN EL CONTROLADOR DE IDIOMA
    val languageController = remember {
        object : LanguageActions {
            override val currentLanguage: String get() = currentLanguageCode

            override fun setLanguage(languageCode: String) {
                // A. Actualiza la UI (Compose se redibuja)
                currentLanguageCode = languageCode

                // B. Actualiza el Repositorio (El Singleton recibe el cambio)
                LanguageConfig.currentCode = languageCode
            }
        }
    }

    // 4. Proveedores de CompositionLocal y Tema
    CompositionLocalProvider(
        LocalThemeActions provides themeController,
        LocalSidebarActions provides sidebarController,
        LocalLanguageActions provides languageController
    ) {
        //MaterialTheme(colorScheme = currentColorScheme) {
        DriverAppTheme(darkTheme = isDarkTheme) {
                // 5. Contenido Principal (Navegador + Sidebar)
                //Row(modifier = Modifier.fillMaxSize()) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                //Row(modifier = Modifier.fillMaxSize()) {
                    // Navegador (Ocupa todo el espacio restante)
                    Navigator(screen = Login){ navigator ->
                        // Aquí es donde sucede la magia:
                        // Guardamos la función en el nivel App para que persista
                        // aunque el sidebar se abra y cierre.

                        // Si la pantalla actual es PrivateDashboard, necesitamos que ella
                        // pueda setear 'recoverActiveScreenFocus'.

                        CurrentScreen()
                    }


                    // Barra Lateral (Panel de Configuración)
                    // AnimatedVisibility(
                    //    visible = isSidebarOpen,
                    //    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    //    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    //) {
                    //    SidebarPanel(modifier = Modifier.width(300.dp)) // Ancho fijo para el panel
                    //}
                //}
            }
        }
    }
}