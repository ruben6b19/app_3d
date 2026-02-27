package com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.presentation.composables.ReusableDropdownSelect
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments.util.EnrollmentFormStrings

@Composable
fun EnrollmentForm(
    viewModel: EnrollmentViewModel,
    texts: EnrollmentFormStrings
) {
    val focusManager = LocalFocusManager.current
    val studentFocusRequester = remember { FocusRequester() }
    val studentOptions = viewModel.availableStudentsToEnroll.associate { it.id to "${it.fullName} (${it.email})" }
    //val isFormEnabled = !viewModel.isLoading
    val isFormEnabled = !viewModel.isFormSubmitting

    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 4.dp,
        //color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(16.dp).fillMaxWidth(0.8f) // Un poco más ancho para nombres largos
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = texts.titleEnroll,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))

            if (studentOptions.isEmpty() && isFormEnabled) {
                // Si la lista está vacía y no estamos cargando (ya cargó y no encontró)
                Text(
                    text = texts.noStudentsFound,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                // 2. Implementación de ReusableDropdownSelect
                ReusableDropdownSelect(
                    labelText = texts.studentLabel,
                    selectedCode = viewModel.selectedStudentId,
                    options = studentOptions,
                    onCodeSelected = viewModel::onStudentSelected,
                    errorText = viewModel.studentSelectionError ?: "",
                    focusRequester = studentFocusRequester,
                    focusManager = focusManager,
                    enabled = isFormEnabled,
                    // 💡 Es el único campo, al seleccionar/terminar, intentamos crear la matrícula.
                    onSelectionDone = viewModel::createEnrollment
                )
            }

            // Si la lista está cargando
            if (viewModel.isFormSubmitting) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                onClick = viewModel::createEnrollment,
                enabled = !viewModel.isFormSubmitting && viewModel.selectedStudentId.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                // Proporcionamos el ícono de Añadir
                icon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = texts.buttonEnroll
                    )
                },
                text = texts.buttonEnroll // Pasamos el texto
            )

            // Mensajes de Error
            viewModel.errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}