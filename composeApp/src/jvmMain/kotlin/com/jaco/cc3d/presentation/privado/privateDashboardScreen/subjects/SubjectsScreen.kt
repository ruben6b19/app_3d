package com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.util.SubjectsResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    viewModel: SubjectsViewModel,
    onLogout: () -> Unit,
    onMenuClick: () -> Unit,
    // << NUEVO: Función de navegación a QuizTemplates >>
    navigateToQuizTemplates: (subjectId: String, subjectName: String) -> Unit
) {
    val languageActions = LocalLanguageActions.current
    val currentLangCode = languageActions.currentLanguage
    val content = SubjectsResources.get(currentLangCode)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentLangCode) { viewModel.lang = currentLangCode }

    // Manejo de Errores y Logout
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

    // Manejo de Éxito
    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let { successKey ->
            val message = when (successKey) {
                "success_subject_deleted" -> content.feedback.successDelete
                "success_subject_created" -> content.feedback.successCreate
                "success_subject_updated" -> content.feedback.successUpdate
                else -> successKey
            }
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            viewModel.clearSuccessMessage()
        }
    }

    if (viewModel.mustLogout) {
        onLogout()
        viewModel.onLogoutHandled()
        return
    }

    ScreenLayout(title = content.list.titleScreen) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {

            val appBarTitle = when (viewModel.uiMode) {
                SubjectsUiMode.LIST -> content.list.titleScreen
                SubjectsUiMode.CREATE -> content.form.titleRegister
                SubjectsUiMode.EDIT -> content.form.titleEdit(viewModel.selectedSubject?.name ?: "")
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (viewModel.uiMode == SubjectsUiMode.LIST) Spacer(modifier = Modifier.width(44.dp))
                                Text(appBarTitle, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        navigationIcon = {
                            if (viewModel.uiMode != SubjectsUiMode.LIST) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(modifier = Modifier.width(44.dp))
                                    IconButton(onClick = viewModel::exitFormMode) {
                                        Icon(Icons.Filled.ArrowBack, contentDescription = content.list.backButton, tint = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            }
                        }
                    )
                },
                floatingActionButton = {
                    if (viewModel.uiMode == SubjectsUiMode.LIST) {
                        FloatingActionButton(onClick = viewModel::enterCreateMode) {
                            Icon(Icons.Filled.Add, contentDescription = content.list.fabCreate)
                        }
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                containerColor = Color.Transparent
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    when (viewModel.uiMode) {
                        SubjectsUiMode.LIST -> if (viewModel.isListLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            SubjectsList(
                                viewModel = viewModel,
                                texts = content.list,
                                navigateToQuizTemplates = navigateToQuizTemplates // << PASANDO LA FUNCIÓN DE NAVEGACIÓN
                            )
                        }
                        SubjectsUiMode.CREATE -> SubjectForm(viewModel, isEditing = false, texts = content.form)
                        SubjectsUiMode.EDIT -> SubjectForm(viewModel, isEditing = true, texts = content.form)
                    }

                    if (viewModel.isCrudLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
            }
        }
    }
}