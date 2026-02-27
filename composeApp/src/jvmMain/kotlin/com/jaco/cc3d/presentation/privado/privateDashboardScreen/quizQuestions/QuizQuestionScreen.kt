package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions

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
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions.util.QuizQuestionResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizQuestionScreen(
    viewModel: QuizQuestionViewModel,
    templateId: String,
    templateName: String,
    language: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val languageActions = LocalLanguageActions.current
    val content = QuizQuestionResources.get(languageActions.currentLanguage)
    val snackbarHostState = remember { SnackbarHostState() }
    val feedbackMessages = content.feedback

    // Establecer contexto
    LaunchedEffect(templateId) {
        viewModel.setTemplateContext(templateId, language)
    }

    // --- Lógica de Mensajes y Logout (Igual a QuizTemplate) ---
    LaunchedEffect(viewModel.errorMessage, viewModel.mustLogout) {
        if (viewModel.mustLogout) {
            viewModel.onLogoutHandled()
            onLogout()
            return@LaunchedEffect
        }

        viewModel.errorMessage?.let { messageKey ->
            // Mapeo de errores conocidos o mostrar el key directamente
            val message = when (messageKey) {
                feedbackMessages.sessionExpired -> feedbackMessages.sessionExpired
                "error_required_fields" -> feedbackMessages.requiredFields
                else -> messageKey
            }
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(viewModel.successMessage) {
        viewModel.successMessage?.let { messageKey ->
            val message = when (messageKey) {
                "success_create" -> feedbackMessages.successCreate
                "success_update" -> feedbackMessages.successUpdate
                "success_delete" -> feedbackMessages.successDelete
                else -> messageKey
            }
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            viewModel.clearSuccessMessage()
        }
    }

    if (viewModel.mustLogout) return

    val appBarTitle = "${content.list.titleScreen(templateName)}"

    ScreenLayout(title = appBarTitle) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(appBarTitle, style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(44.dp))
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = content.list.backButton
                                )
                            }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                if (!viewModel.isFormOpen) {
                    ExtendedFloatingActionButton(
                        onClick = viewModel::openCreateForm,
                        icon = { Icon(Icons.Filled.Add, null) },
                        text = { Text(content.list.fabCreate) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // 1. Panel Formulario (Izquierda)
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (viewModel.isFormOpen) {
                        QuizQuestionForm(
                            viewModel = viewModel,
                            texts = content.form,
                            templateName = templateName
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Selecciona una pregunta para editar o crea una nueva",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Divisor Vertical
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                // 2. Panel Lista (Derecha)
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isListLoading) {
                        CircularProgressIndicator()
                    } else {
                        QuizQuestionList(viewModel, content.list)
                    }
                }
            }
        }
    }
}