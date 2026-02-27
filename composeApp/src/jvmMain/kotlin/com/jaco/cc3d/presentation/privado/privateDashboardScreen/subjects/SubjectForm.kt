package com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.presentation.composables.FocusAwareTextField
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.util.SubjectFormStrings
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SubjectForm(
    viewModel: SubjectsViewModel,
    isEditing: Boolean,
    texts: SubjectFormStrings
) {
    val focusManager = LocalFocusManager.current
    val (rName, rDesc) = remember { FocusRequester.createRefs() }

    val title = if (isEditing) texts.titleEdit(viewModel.selectedSubject?.name ?: "") else texts.titleRegister
    val buttonText = if (isEditing) texts.buttonSave else texts.buttonCreate
    val buttonIcon: ImageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Add
    val isFormEnabled = !viewModel.isCrudLoading

    val onFormSubmit: () -> Unit = {
        if (!viewModel.isCrudLoading && viewModel.validateInput()) {
            if (isEditing) viewModel.updateSubject() else viewModel.createSubject()
        }
    }

    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(16.dp).fillMaxWidth(0.6f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))

            // 1. Nombre (Campo normal, singleLine por defecto)
            FocusAwareTextField(
                value = viewModel.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(texts.fieldName) },
                error = viewModel.nameError,
                focusRequester = rName,
                nextFocusRequester = rDesc,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = isFormEnabled
            )
            Spacer(Modifier.height(16.dp))

            // 2. Descripción (Campo Multilínea)
            FocusAwareTextField(
                value = viewModel.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(texts.fieldDescription) },
                error = viewModel.descriptionError,
                focusRequester = rDesc,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(), // Ya no necesitamos definir onDone aquí
                //onDoneAction = onFormSubmit,       // ⬅️ ¡Nueva propiedad para el envío!

                enabled = isFormEnabled
            ){ onFormSubmit() }
            Spacer(Modifier.height(24.dp))

            // Botón
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

            // Error Message
            viewModel.errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}