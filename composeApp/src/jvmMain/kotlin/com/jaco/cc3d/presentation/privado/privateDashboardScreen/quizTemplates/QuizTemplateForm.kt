package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.presentation.composables.ReusableDropdownSelect
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates.util.QuizTemplateFormStrings

@Composable
fun QuizTemplateForm(
    viewModel: QuizTemplateViewModel,
    texts: QuizTemplateFormStrings,
    subjectName: String
) {
    val focusManager = LocalFocusManager.current
    val (rName, rLanguage) = remember { FocusRequester.createRefs() }

    val isEditing = viewModel.isEditing
    val title = if (isEditing) texts.titleEdit(viewModel.templateToEdit?.name ?: "") else texts.titleRegister
    val buttonText = texts.buttonSave
    val isFormEnabled = !viewModel.isFormSubmitting

    val onFormSubmit: () -> Unit = {
        if (!viewModel.isFormSubmitting) {
            viewModel.saveQuizTemplate()
        }
    }

    val buttonIcon: ImageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Add

    // 💡 Contenedor Box para centrar el formulario en la pantalla
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shadowElevation = 8.dp,
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            // 💡 Limitar el ancho al 60% para que no ocupe toda la pantalla
            modifier = Modifier.padding(16.dp).fillMaxWidth(0.6f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título estilizado como en Subjects
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(24.dp))

                // 1. Campo Materia (Solo lectura)
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(texts.subjectLabel) },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                // 2. Campo Nombre (Input dinámico)
                OutlinedTextField(
                    value = viewModel.nameInput,
                    onValueChange = { viewModel.nameInput = it },
                    label = { Text(texts.nameLabel) },
                    placeholder = { Text(texts.namePlaceholder) },
                    isError = viewModel.nameError != null,
                    supportingText = viewModel.nameError?.let { { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(rName),
                    enabled = isFormEnabled,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(Modifier.height(16.dp))

                // 3. Campo Idioma (Dropdown con foco)
                ReusableDropdownSelect(
                    labelText = texts.languageLabel,
                    selectedCode = viewModel.languageInput,
                    options = mapOf("es" to "Español", "en" to "English"),
                    onCodeSelected = { viewModel.languageInput = it },
                    errorText = "",
                    focusRequester = rLanguage,
                    focusManager = focusManager,
                    enabled = isFormEnabled,
                    // 💡 Llama a onFormSubmit al presionar "Done"
                    onSelectionDone = { onFormSubmit() }
                )

                Spacer(Modifier.height(24.dp))

                // Spinner de carga
                if (viewModel.isFormSubmitting) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                }

                // Botón Principal reducido (50% del ancho del card)
                PrimaryButton(
                    onClick = onFormSubmit,
                    enabled = isFormEnabled,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    icon = {
                        Icon(
                            imageVector = buttonIcon,
                            contentDescription = buttonText
                        )
                    },
                    text = buttonText
                )

                Spacer(Modifier.height(8.dp))

                // Botón Cancelar opcional
                if (isEditing || viewModel.isFormOpen) {
                    TextButton(
                        onClick = viewModel::closeForm,
                        enabled = isFormEnabled
                    ) {
                        Text(texts.buttonCancel)
                    }
                }

                // Mensajes de Error Global
                viewModel.errorMessage?.let { message ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}