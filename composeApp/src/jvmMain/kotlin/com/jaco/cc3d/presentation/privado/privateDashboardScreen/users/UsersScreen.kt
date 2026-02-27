// com/jaco/cc3d/presentation/privado/users/UsersScreen.kt

package com.jaco.cc3d.presentation.privado.privateDashboardScreen.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.util.UsersResources // Importación correcta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit, // Función para volver a la pantalla de Institutos
    instituteId: String // ID del instituto seleccionado
) {

    val languageActions = LocalLanguageActions.current
    val currentLangCode = languageActions.currentLanguage

    val content = UsersResources.get(currentLangCode)
    val listTexts = content.list
    val formTexts = content.form
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentLangCode) {
        viewModel.lang = currentLangCode
    }

    // 💡 SINCRONIZACIÓN DEL ID DEL INSTITUTO
    LaunchedEffect(instituteId) {
        viewModel.setInstituteId(instituteId)
    }

    LaunchedEffect(viewModel.errorMessage, viewModel.mustLogout) {
        if (viewModel.mustLogout) {
            viewModel.onLogoutHandled()
            onLogout()
            return@LaunchedEffect
        }
        viewModel.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
            viewModel.clearErrorMessage()
        }
    }

    ScreenLayout(title = listTexts.titleScreen) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {

            val appBarTitle = when (viewModel.uiMode) {
                UsersUiMode.LIST -> listTexts.titleScreen
                UsersUiMode.CREATE -> formTexts.titleRegister
                UsersUiMode.EDIT -> formTexts.titleEdit(viewModel.selectedUser?.fullName ?: "")
            }

            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text(appBarTitle, color = MaterialTheme.colorScheme.onPrimary) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        navigationIcon = {
                            // Si estamos en lista, volver a institutos; si estamos en formulario, salir del formulario.
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(44.dp))
                                IconButton(onClick = if (viewModel.uiMode == UsersUiMode.LIST) onBack else viewModel::exitFormMode) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = listTexts.backButton, tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                    )
                },
                floatingActionButton = {
                    if (viewModel.uiMode == UsersUiMode.LIST) {
                        FloatingActionButton(onClick = viewModel::enterCreateMode) {
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
                        // 2. CONTENIDO (Solo si NO está cargando)
                        when (viewModel.uiMode) {
                            UsersUiMode.LIST -> UsersList(viewModel = viewModel, texts = listTexts)
                            UsersUiMode.CREATE -> UserForm(viewModel = viewModel, isEditing = false, texts = formTexts)
                            UsersUiMode.EDIT -> UserForm(viewModel = viewModel, isEditing = true, texts = formTexts)
                        }
                    }
                }
            }
        }
    }
}