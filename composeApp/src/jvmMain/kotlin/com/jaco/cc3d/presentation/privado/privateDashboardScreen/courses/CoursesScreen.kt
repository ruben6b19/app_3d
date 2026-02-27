package com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
// Importaciones de Material 3
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses.util.CoursesResources
// Se asume que CourseList y CourseForm existen en este paquete

/**
 * Screen principal para la gestión de Cursos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit, // Función para volver a la pantalla de Institutos
    instituteId: String, // ID del instituto seleccionado
    instituteName: String,
    navigateToEnrollment: (Course) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val languageActions = LocalLanguageActions.current
    val currentLangCode = languageActions.currentLanguage

    // Sincroniza el idioma con el de la aplicación
    LaunchedEffect(currentLangCode) {
        viewModel.lang = currentLangCode
    }

    // 💡 SINCRONIZACIÓN DEL ID DEL INSTITUTO
    LaunchedEffect(instituteId, instituteName) {
        // Usamos la función actualizada del ViewModel
        viewModel.initializeInstituteFilter(instituteId, instituteName)
    }

    val currentContent = CoursesResources.get(viewModel.lang)
    val listTexts = currentContent.list
    val formTexts = currentContent.form

    // Manejo de mensajes de éxito
    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccessMessage()
        }
    }

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

    val instituteDisplay = viewModel.instituteName?.let { " ($it)" } ?: ""

    val appBarTitle = when (viewModel.uiMode) {
        // 💡 CONCATENAR el título de la lista con el nombre del instituto
        CoursesUiMode.LIST -> listTexts.titleScreen + instituteDisplay
        CoursesUiMode.CREATE -> formTexts.titleRegister
        CoursesUiMode.EDIT -> formTexts.titleEdit(viewModel.selectedCourse?.group ?: "")
    }

    ScreenLayout(title = listTexts.titleScreen) { // Usamos ScreenLayout si es el patrón general
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text(appBarTitle, color = MaterialTheme.colorScheme.onPrimary) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        navigationIcon = {
                            // 💡 LÓGICA DE VOLVER ATRÁS IDÉNTICA A UsersScreen
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(44.dp))
                                IconButton(onClick = if (viewModel.uiMode == CoursesUiMode.LIST) onBack else viewModel::exitFormMode) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = listTexts.backButton, tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                    )
                },
                floatingActionButton = {
                    if (viewModel.uiMode == CoursesUiMode.LIST) {
                        FloatingActionButton(
                            onClick = viewModel::enterCreateMode,
                            //containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = listTexts.fabCreate)
                        }
                    }
                },
                containerColor = Color.Transparent
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    if (viewModel.isLoading) {
                        // El Box aquí ya usa el padding, por lo que cubre toda la zona debajo del TopAppBar.
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer) // Usar un color visible
                        }
                    } else {
                        // CONTENIDO (Solo si NO está cargando)
                        when (viewModel.uiMode) {
                            CoursesUiMode.LIST -> CoursesList(
                                viewModel = viewModel,
                                texts = listTexts,
                                navigateToEnrollment = navigateToEnrollment
                            )
                            CoursesUiMode.CREATE -> CourseForm(
                                viewModel = viewModel,
                                isEditing = false,
                                texts = formTexts
                            )
                            CoursesUiMode.EDIT -> CourseForm(
                                viewModel = viewModel,
                                isEditing = true,
                                texts = formTexts
                            )
                        }
                    }
                }
            }
        }
    }
}