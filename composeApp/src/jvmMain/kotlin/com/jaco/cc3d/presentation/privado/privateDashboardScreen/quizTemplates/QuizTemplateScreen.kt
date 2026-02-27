package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates

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
import com.jaco.cc3d.domain.models.QuizTemplate
import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates.util.QuizTemplateResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTemplateScreen(
    viewModel: QuizTemplateViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    subjectId: String,
    subjectName: String,
    onNavigateToQuestions: (QuizTemplate) -> Unit
) {
    val languageActions = LocalLanguageActions.current
    val content = QuizTemplateResources.get(languageActions.currentLanguage)
    val snackbarHostState = remember { SnackbarHostState() }
    val feedbackMessages = content.feedback

    LaunchedEffect(subjectId) {
        viewModel.setSubjectContext(subjectId)
    }

    // --- Manejo de mensajes y Logout ---
    LaunchedEffect(viewModel.errorMessage, viewModel.mustLogout) {
        if (viewModel.mustLogout) {
            viewModel.onLogoutHandled()
            onLogout()
            return@LaunchedEffect
        }
        viewModel.errorMessage?.let { messageKey ->
            snackbarHostState.showSnackbar(messageKey, duration = SnackbarDuration.Long)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let { messageKey ->
            // BUSCAMOS LA TRADUCCIÓN REAL BASADA EN LA LLAVE
            val message = when (messageKey) {
                "success_create" -> feedbackMessages.successCreate
                "success_update" -> feedbackMessages.successUpdate
                "success_delete" -> feedbackMessages.successDelete
                else -> messageKey // Por si acaso
            }

            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            viewModel.clearSuccessMessage()
        }
    }

    if (viewModel.mustLogout) return

    // Título dinámico basado en el modo

    val appBarTitle = when (viewModel.uiMode) {
        QuizTemplateUiMode.LIST -> "${content.list.titleScreen} - $subjectName"
        QuizTemplateUiMode.CREATE -> content.form.titleRegister
        QuizTemplateUiMode.EDIT -> content.form.titleEdit(viewModel.templateToEdit?.name ?: "")
    }

    ScreenLayout(title = appBarTitle) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (viewModel.uiMode == QuizTemplateUiMode.LIST) {
                                    Spacer(modifier = Modifier.width(44.dp))
                                }
                                Text(appBarTitle)
                            }
                        },
                        navigationIcon = {
                            // 💡 Icono de navegación ajustado al estilo de Subjects
                            if (viewModel.uiMode != QuizTemplateUiMode.LIST) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(modifier = Modifier.width(44.dp))
                                    IconButton(onClick = viewModel::closeForm) {
                                        Icon(Icons.Filled.ArrowBack, contentDescription = content.list.backButton)
                                    }
                                }
                            } else {
                                // En modo lista, el botón back lleva a Subjects
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(modifier = Modifier.width(44.dp))
                                    IconButton(onClick = onBack) {
                                        Icon(Icons.Filled.ArrowBack, contentDescription = content.list.backButton)
                                    }
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
                snackbarHost = { SnackbarHost(snackbarHostState) },
                floatingActionButton = {
                    if (viewModel.uiMode == QuizTemplateUiMode.LIST) {
                        FloatingActionButton(onClick = viewModel::openCreateForm) {
                            Icon(Icons.Filled.Add, contentDescription = content.list.fabCreate)
                        }
                    }
                },
                containerColor = androidx.compose.ui.graphics.Color.Transparent // Igual que Subjects
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    when (viewModel.uiMode) {
                        QuizTemplateUiMode.LIST -> {
                            if (viewModel.isListLoading) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                QuizTemplateList(
                                    viewModel = viewModel,
                                    texts = content.list,
                                    onNavigateToQuestions = onNavigateToQuestions
                                )
                            }
                        }
                        // 💡 Los formularios ahora se renderizan directamente,
                        // el diseño (0.6f) lo maneja internamente QuizTemplateForm
                        QuizTemplateUiMode.CREATE -> {
                            QuizTemplateForm(
                                viewModel = viewModel,
                                texts = content.form,
                                subjectName = subjectName
                            )
                        }
                        QuizTemplateUiMode.EDIT -> {
                            QuizTemplateForm(
                                viewModel = viewModel,
                                texts = content.form,
                                subjectName = subjectName
                            )
                        }
                    }

                    // Overlay de carga CRUD
                    if (viewModel.isFormSubmitting) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Función auxiliar para el divisor vertical
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