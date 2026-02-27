package com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments.util.EnrollmentResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentScreen(
    viewModel: EnrollmentViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    courseName: String // Para mostrar en el título
) {
    val languageActions = LocalLanguageActions.current
    val content = EnrollmentResources.get(languageActions.currentLanguage)
    val snackbarHostState = remember { SnackbarHostState() }

    // Manejo de mensajes (Éxito/Error)
    // ... (Similar a CourseScreen)

    val appBarTitle = "${content.list.titleScreen} - $courseName"

    LaunchedEffect(viewModel.errorMessage, viewModel.mustLogout) {
        if (viewModel.mustLogout) {
            viewModel.onLogoutHandled()
            onLogout()
            return@LaunchedEffect
        }
        viewModel.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            viewModel.clearErrorMessage()
        }
    }

    ScreenLayout(title = appBarTitle) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(appBarTitle, style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(44.dp)) // Ajuste para el ícono de vuelta
                            IconButton(onClick = {
                                // Aquí puedes mantener la lógica anterior o simplificarla
                                // dependiendo de si el formulario se cierra al hacer clic en 'atrás'
                                // Si quieres que 'atrás' siempre regrese, independientemente del form:
                                onBack()

                                // O si quieres que cierre el form primero:
                                // if (viewModel.isFormOpen) viewModel.closeForm() else onBack()
                            }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = content.list.backButton)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            // El FAB ahora solo tiene sentido si quieres un botón para **abrir** el formulario,
            // pero si siempre está visible, podrías considerar quitarlo.
            floatingActionButton = {
                // Lo quitamos o cambiamos su funcionalidad, ya que el Formulario puede estar visible.
                // Si quieres que el FAB abra el formulario:
                /*
                if (!viewModel.isFormOpen) {
                    FloatingActionButton(onClick = viewModel::openForm) {
                        Icon(Icons.Filled.Add, contentDescription = content.list.fabEnroll)
                    }
                }
                */
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            // --- CAMBIO CLAVE: Usamos Row para dividir la pantalla ---
            Row(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // 1. Formulario de Inscripción (Izquierda - 1/3 del ancho)
                Box(
                    modifier = Modifier
                        .weight(1f) // Ocupa 1 parte (e.g., 1/3)
                        .fillMaxHeight()
                        .padding(8.dp),
                    contentAlignment = Alignment.TopCenter // Alinea el formulario arriba
                ) {
                    // El formulario SIEMPRE está visible en esta nueva distribución.
                    // Si quieres que el formulario se muestre/oculte, usa un 'if' aquí
                    // if (viewModel.isFormOpen) { ... }
                    EnrollmentForm(viewModel, content.form)
                }

                // Separador visual (opcional)
                VerticalDivider()

                // 2. Lista de Alumnos (Derecha - 2/3 del ancho)
                Box(
                    modifier = Modifier
                        .weight(2f) // Ocupa 2 partes (e.g., 2/3)
                        .fillMaxHeight(),
                    // Centramos el contenido dentro de este Box (útil para el spinner)
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isEnrollmentListLoading) {
                        // Si está cargando, solo mostramos el spinner en este panel
                        CircularProgressIndicator()
                    } else {
                        // Si no está cargando, mostramos la lista de alumnos
                        EnrollmentList(viewModel, content.list)
                    }
                }
            }
            // --------------------------------------------------------
        }
    }
}

// Puedes añadir esta función si no la tienes, para un separador visual
@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .width(1.dp)
            .fillMaxHeight()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}