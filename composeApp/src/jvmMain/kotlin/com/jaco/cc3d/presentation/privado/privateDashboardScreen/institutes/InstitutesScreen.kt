package com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
// Importaciones de Material 3
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults // Necesario para elevación de TopAppBar

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

import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.FeedbackMessages
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.InstitutesResources

// La pantalla principal que decide qué vista mostrar. (M3)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutesScreen(
    viewModel: InstitutesViewModel,
    onLogout: () -> Unit,
    navigateToUsers: (String) -> Unit,
    navigateToCourses: (String, String) -> Unit,
    onMenuClick: () -> Unit
) {

    val languageActions = LocalLanguageActions.current
    val currentLangCode = languageActions.currentLanguage

    val content = InstitutesResources.get(currentLangCode)
    val listTexts = content.list
    val formTexts = content.form
    val feedbackTexts: FeedbackMessages = content.feedback
    val snackbarHostState = remember { SnackbarHostState() } //

    //val texts = Texts.getStrings(currentLangCode)
    //val listTexts = ListTexts.getStrings(currentLangCode)

    LaunchedEffect(currentLangCode) {
        viewModel.lang = currentLangCode
    }
    // Efecto para manejar el feedback (Errores y Éxito)
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

    // Efecto para manejar el feedback de éxito
    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let { successKey ->
            val message = when (successKey) {
                "success_institute_deleted" -> feedbackTexts.successDelete
                "success_institute_created" -> feedbackTexts.successCreate
                "success_institute_updated" -> feedbackTexts.successUpdate
                else -> successKey // Por si acaso
            }

            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            // Limpiar el mensaje de éxito después de mostrarlo
            viewModel.clearSuccessMessage()
        }
    }

    if (viewModel.mustLogout) {
        println("Ejecuta el deslogueo (navegación)")
        onLogout() // Navega fuera de la pantalla privada (hacia el login)
        viewModel.onLogoutHandled() // Restablece el flag
        return
    }


    ScreenLayout(title = listTexts.titleScreen) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
        ) {

            // 💡 Lógica del título de la TopAppBar (Usa ListTexts para el modo LIST)
            val appBarTitle = when (viewModel.uiMode) {
                InstitutesUiMode.LIST -> listTexts.titleScreen
                InstitutesUiMode.CREATE -> formTexts.titleRegister // Usamos formTexts
                InstitutesUiMode.EDIT -> formTexts.titleEdit(viewModel.selectedInstitute?.name ?: "")
            }

            Scaffold( // M3 Scaffold
                topBar = {

                    TopAppBar( // M3 TopAppBar
                        title = {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Si estamos en modo LIST y NO hay icono de navegación,
                                // agregamos un espaciador para empujar el título
                                if (viewModel.uiMode == InstitutesUiMode.LIST) {
                                    // Añade un espacio similar al ancho estándar de un navigationIcon
                                    Spacer(modifier = Modifier.width(44.dp))
                                }

                                Text(
                                    appBarTitle, // 💡 Título dinámico
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                        navigationIcon = {
                            if (viewModel.uiMode != InstitutesUiMode.LIST) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(modifier = Modifier.width(44.dp))
                                    IconButton(onClick = viewModel::exitFormMode) { // M3 IconButton
                                        val tintColor = MaterialTheme.colorScheme.onPrimary
                                        val icon = Icons.Filled.ArrowBack
                                        // 💡 Descripción del contenido del botón de volver
                                        Icon(icon, contentDescription = listTexts.backButton, tint = tintColor)
                                    }
                                }
                            }
                        }
                    )
                },
                floatingActionButton = {
                    if (viewModel.uiMode == InstitutesUiMode.LIST) {
                        FloatingActionButton(onClick = viewModel::enterCreateMode) { // M3 FloatingActionButton
                            // 💡 Descripción del contenido del FAB
                            Icon(Icons.Filled.Add, contentDescription = listTexts.fabCreate)
                        }
                    }
                },
                containerColor = Color.Transparent,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    when (viewModel.uiMode) {
                        // 3. PASAMOS LOS TEXTOS POR PARÁMETRO
                        InstitutesUiMode.LIST -> InstitutesList(
                            viewModel = viewModel,
                            texts = listTexts, // Pasamos solo la parte de lista
                            navigateToUsers = navigateToUsers,
                            navigateToCourses = navigateToCourses
                        )
                        InstitutesUiMode.CREATE -> InstituteForm(
                            viewModel = viewModel,
                            isEditing = false,
                            texts = formTexts // Pasamos solo la parte de formulario
                        )
                        InstitutesUiMode.EDIT -> InstituteForm(
                            viewModel = viewModel,
                            isEditing = true,
                            texts = formTexts // Pasamos solo la parte de formulario
                        )
                    }

                    // Overlay de carga
                    if (viewModel.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White) // M3 CircularProgressIndicator
                        }
                    }
                }
            }
        }
    }
}