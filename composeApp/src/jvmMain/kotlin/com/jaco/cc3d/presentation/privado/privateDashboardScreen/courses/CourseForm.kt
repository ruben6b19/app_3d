package com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add // Para Crear
import androidx.compose.material.icons.filled.Save // Para Guardar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.presentation.composables.ErrorRetryItem
import com.jaco.cc3d.presentation.composables.FocusAwareTextField
import com.jaco.cc3d.presentation.composables.ReusableDropdownSelect
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton // <-- Importación necesaria
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses.util.CourseFormStrings
// Importar FocusAwareTextField y CourseFormStrings deben estar disponibles en el proyecto

@Composable
fun CourseForm(
    viewModel: CoursesViewModel,
    isEditing: Boolean,
    texts: CourseFormStrings
) {
    val focusManager = LocalFocusManager.current

    val (rInstituteId, rSubjectId, rTeacherId, rAcademicYear, rGroup) = remember { FocusRequester.createRefs() }

    val title = if (isEditing) texts.titleEdit(viewModel.selectedCourse?.group ?: "") else texts.titleRegister
    val buttonText = if (isEditing) texts.buttonSave else texts.buttonCreate
    val isFormEnabled = !viewModel.isLoading
    val subjectOptions = viewModel.availableSubjects.associate { it.id to it.name }
    val teacherOptions = viewModel.availableTeachers.associate { it.id to it.fullName }

    val onFormSubmit: () -> Unit = {
        if (!viewModel.isLoading && viewModel.validateInput()) {
            if (isEditing) viewModel.updateCourse() else viewModel.createCourse()
        }
    }

    // Definimos el ícono basado en si estamos editando o creando
    val buttonIcon: ImageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Add

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

            // 1. Institute ID (Comentado/Placeholder en el código original, pero lo mantenemos)
            // ...

            if (viewModel.subjectsLoadError != null) {
                // CASO ERROR: Mostrar mensaje y botón de reintentar
                ErrorRetryItem(
                    errorText = "Error cargando materias",
                    onRetry = viewModel::retryLoadSubjects
                )
            } else {
                ReusableDropdownSelect(
                    labelText = texts.fieldSubject,
                    selectedCode = viewModel.subjectId,
                    options = subjectOptions,
                    onCodeSelected = viewModel::onSubjectIdChange,
                    errorText = viewModel.subjectIdError ?: "",
                    focusRequester = rSubjectId,
                    focusManager = focusManager,
                    enabled = isFormEnabled,
                    // Al seleccionar, movemos el foco al siguiente campo (Teacher ID)
                    onSelectionDone = { focusManager.moveFocus(FocusDirection.Down) }
                )
            }
            Spacer(Modifier.height(16.dp))
            if (viewModel.teachersLoadError != null) {
                ErrorRetryItem(
                    errorText = "Error cargando profesores",
                    onRetry = viewModel::retryLoadTeachers
                )
            } else {
                ReusableDropdownSelect(
                    labelText = texts.fieldTeacher,
                    selectedCode = viewModel.teacherId,
                    options = teacherOptions, // 💡 Usamos las opciones de profesores
                    onCodeSelected = viewModel::onTeacherIdChange,
                    errorText = viewModel.teacherIdError ?: "",
                    focusRequester = rTeacherId,
                    focusManager = focusManager,
                    enabled = isFormEnabled,
                    // Al seleccionar, movemos el foco al siguiente campo (Academic Year)
                    onSelectionDone = { focusManager.moveFocus(FocusDirection.Down) }
                )
            }
            Spacer(Modifier.height(16.dp))

            // 4. Academic Year
            FocusAwareTextField(
                value = viewModel.academicYear,
                onValueChange = viewModel::onAcademicYearChange,
                label = { Text(texts.fieldAcademicYear) },
                error = viewModel.academicYearError,
                focusRequester = rAcademicYear,
                nextFocusRequester = rGroup,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = isFormEnabled
            )
            Spacer(Modifier.height(16.dp))

            // 5. Group
            FocusAwareTextField(
                value = viewModel.group,
                onValueChange = viewModel::onGroupChange,
                label = { Text(texts.fieldGroup) },
                error = viewModel.groupError,
                focusRequester = rGroup,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    // Al presionar 'Done' en el último campo, se ejecuta la acción de envío
                    onDone = { onFormSubmit() }
                ),
                enabled = isFormEnabled
            )

            Spacer(Modifier.height(24.dp))

            // --- REEMPLAZO POR PRIMARY BUTTON ---
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
            // ------------------------------------

            // Error Message
            viewModel.errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}