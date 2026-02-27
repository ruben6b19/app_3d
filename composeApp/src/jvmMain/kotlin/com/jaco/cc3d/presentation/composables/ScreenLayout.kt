package com.jaco.cc3d.presentation.composables

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.LocalSidebarActions
// Necesitarás tener tu SidebarPanel definido y accesible
// import com.jaco.cc3d.presentation.composables.SidebarPanel // Asumiendo que está en el paquete raíz o importado

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScreenLayout(
    title: String,
    content: @Composable () -> Unit // El contenido específico de la pantalla (Display, StudentDisplay)
) {
    val sidebarActions = LocalSidebarActions.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 1. Contenido de la Pantalla (Ocupa el 100% del Box)
        content() // Renderiza DisplayScreen o StudentDisplayScreen

        // 🔑 2. BOTÓN DE MENÚ FLOTANTE (Superior IZQUIERDA)
        Surface(
            modifier = Modifier
                // 🔑 CAMBIO CLAVE: Alineado a la esquina superior IZQUIERDA
                .align(Alignment.TopStart)
                .padding(8.dp),
            shape = MaterialTheme.shapes.small,
            shadowElevation = 4.dp
        ) {
            IconButton(onClick = sidebarActions::toggleSidebar) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Abrir Menú de Configuración",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 3. PANEL LATERAL ANIMADO (Alineado a la izquierda, debajo del botón)
        AnimatedVisibility(
            visible = sidebarActions.isOpen,
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(300.dp)
                .fillMaxHeight(),
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut()
        ) {
            // Usamos Surface para que el fondo del panel se adapte al tema y para la elevación/sombra
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    // 🔑 AJUSTE: Aplicamos un padding superior para evitar que se superponga
                    // a la altura del botón que está arriba. Un valor fijo de 56dp o 64dp
                    // suele ser suficiente para una TopBar o un botón flotante.
                    .padding(top = 56.dp),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 8.dp
            ) {
                SidebarPanel() // El composable de configuración
            }
        }
    }

}