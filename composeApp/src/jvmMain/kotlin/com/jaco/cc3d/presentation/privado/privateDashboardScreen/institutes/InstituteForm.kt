package com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.ui.focus.FocusRequester
import com.jaco.cc3d.presentation.composables.FocusAwareTextField
import com.jaco.cc3d.presentation.composables.ReusableDateField
import com.jaco.cc3d.presentation.composables.ReusableDropdownSelect
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.InstituteFormStrings
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.CityOptions
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.LanguageOptions
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.vector.ImageVector

// ----------------------------------------------------
// 2 & 3. CREATE & UPDATE: Composable para el formulario (M3)
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstituteForm(viewModel: InstitutesViewModel, isEditing: Boolean, texts: InstituteFormStrings) {
    val focusManager = LocalFocusManager.current

    // ** Focus Requesters para el control explícito del orden **
    val (r1, r2, r3, r4) = remember { FocusRequester.createRefs() }

    val title = if (isEditing) texts.titleEdit(viewModel.selectedInstitute?.name ?: "") else texts.titleRegister
    val buttonText = if (isEditing) texts.buttonSave else texts.buttonCreate
    val buttonIcon: ImageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Add

    // Lógica de envío centralizada (llama a la validación antes de la acción)
    val onFormSubmit: () -> Unit = {
        // Solo intenta enviar si no está cargando actualmente
        if (!viewModel.isLoading && viewModel.validateInput()) {
            if (isEditing) {
                viewModel.updateInstitute()
            } else {
                viewModel.createInstitute()
            }
        }
    }

    // Estado para deshabilitar todas las entradas
    val isFormEnabled = !viewModel.isLoading

    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(0.6f)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))

            FocusAwareTextField(
                value = viewModel.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(texts.fieldName) },
                error = viewModel.nameError,
                focusRequester = r1,
                nextFocusRequester = r2,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = isFormEnabled // ⬅️ Deshabilitado durante la carga
            )
            Spacer(Modifier.height(16.dp))

            // 2. Fecha de Fundación (r2) -> Siguiente r3
            ReusableDateField(
                value = viewModel.foundationDate,
                onValueChange = viewModel::onFoundationDateChange,
                labelText = texts.fieldFoundationDate,
                error = viewModel.foundationDateError,
                focusRequester = r2,
                nextFocusRequester = r3, // Pasar el siguiente focus requester (r3)

                imeAction = ImeAction.Next, // Muestra 'Siguiente' en el teclado
                keyboardActions = KeyboardActions(
                    // Al presionar 'Siguiente', mueve el foco al siguiente FocusRequester (r3)
                    onNext = { r3.requestFocus() }
                ),
                enabled = isFormEnabled // ⬅️ Deshabilitado durante la carga
            )
            Spacer(Modifier.height(16.dp))

            // 3. Código de Ciudad (r3) -> Siguiente r4
            ReusableDropdownSelect(
                labelText = texts.fieldCity,
                selectedCode = viewModel.cityCodeInput,
                options = CityOptions,
                onCodeSelected = viewModel::onCityCodeChange,
                errorText = viewModel.cityCodeInputError,
                focusRequester = r3,
                focusManager = focusManager,
                enabled = isFormEnabled // ⬅️ Deshabilitado durante la carga
            )
            Spacer(Modifier.height(16.dp))

            // 4. Idioma (r4) -> Último campo
            ReusableDropdownSelect(
                labelText = texts.fieldLanguage,
                selectedCode = viewModel.language,
                options = LanguageOptions,
                onCodeSelected = viewModel::onLanguageChange,
                errorText = viewModel.languageError ?: "",
                focusRequester = r4,
                focusManager = focusManager,
                enabled = isFormEnabled, // ⬅️ Deshabilitado durante la carga
            ) { onFormSubmit() }
            Spacer(Modifier.height(24.dp))

            // --- Botón de Acción (Implementación de prevención de doble clic con feedback) ---
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
            Spacer(Modifier.height(16.dp))

            // --- Mensajes de Feedback ---
            viewModel.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
            }

            //if (viewModel.isSuccess) {
            //    Text(
            //        text = texts.success,
            //        color = Color(0xFF4CAF50),
            //        style = MaterialTheme.typography.bodyMedium
            //    )
            //}
        }
    }
}