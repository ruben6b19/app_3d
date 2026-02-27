package com.jaco.cc3d.presentation.privado.privateDashboardScreen.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusRequester
import com.jaco.cc3d.presentation.composables.FocusAwareTextField
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.util.UserFormStrings
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.vector.ImageVector
// ... otras importaciones
// ----------------------------------------------------
// Opciones para el UI (para los botones de selección)
// ----------------------------------------------------
val RoleOptionsMap = mapOf(
    1 to "Profesor", // Asumiendo que 1 es el rol más común a asignar
    0 to "Estudiante",
    2 to "Administrador"
)

val StatusOptionsMap = mapOf(
    1 to "Activo",
    0 to "Inactivo",
    2 to "Bloqueado"
)


// ----------------------------------------------------
// 2 & 3. CREATE & UPDATE: Composable para el formulario de usuario
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserForm(viewModel: UsersViewModel, isEditing: Boolean, texts: UserFormStrings) {
    val focusManager = LocalFocusManager.current

    // ** Focus Requesters **
    // Definimos 4 requesters: r1, r2 (Email, FullName) y r3 (Password), aunque solo usamos r1, r2, r3
    val (r1, r2, r3) = remember { FocusRequester.createRefs() }

    val title = if (isEditing) texts.titleEdit(viewModel.selectedUser?.fullName ?: "") else texts.titleRegister
    val buttonText = if (isEditing) texts.buttonSave else texts.buttonCreate
    val buttonIcon: ImageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Add

    // Lógica de envío centralizada
    val onFormSubmit: () -> Unit = {
        // Solo intentamos enviar si no está cargando actualmente
        if (!viewModel.isLoading && viewModel.validateInput()) {
            if (isEditing) {
                viewModel.updateUser()
            } else {
                viewModel.createUser()
            }
        }
    }

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
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))

            // --- Campos de Entrada ---

            // 1. Email
            FocusAwareTextField(
                value = viewModel.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(texts.fieldEmail) },
                error = viewModel.emailError,
                focusRequester = r1,
                nextFocusRequester = r2,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !viewModel.isLoading // Deshabilitar mientras carga
            )
            Spacer(Modifier.height(16.dp))

            // 2. Nombre Completo
            FocusAwareTextField(
                value = viewModel.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = { Text(texts.fieldFullName) },
                error = viewModel.fullNameError,
                focusRequester = r2,
                nextFocusRequester = r3, // Mover foco a la contraseña
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = !viewModel.isLoading // Deshabilitar mientras carga
            )
            Spacer(Modifier.height(24.dp))

            // 3. Contraseña
            FocusAwareTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(texts.fieldPassword) },
                error = viewModel.passwordError,
                focusRequester = r3,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                enabled = !viewModel.isLoading // Deshabilitar mientras carga
            )
            Spacer(Modifier.height(24.dp))

            Divider()
            Spacer(Modifier.height(24.dp))

            // 3. Roles (Multi-select Toggle Buttons)
            Text(texts.fieldRole, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoleOptionsMap.forEach { (code, name) ->
                    Button(
                        onClick = { viewModel.onRoleChange(code) },
                        enabled = !viewModel.isLoading, // Deshabilitar mientras carga
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.roleInput.contains(code)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (viewModel.roleInput.contains(code)) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(name, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            viewModel.roleError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(24.dp))

            // 4. Estado (Single-select Toggle Buttons)
            Text(texts.fieldStatus, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusOptionsMap.forEach { (code, name) ->
                    Button(
                        onClick = { viewModel.onStatusChange(code) },
                        enabled = !viewModel.isLoading, // Deshabilitar mientras carga
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.statusInput == code) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (viewModel.statusInput == code) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(name, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            viewModel.statusError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(24.dp))

            // --- Botón de Acción ---
            PrimaryButton(
                onClick = onFormSubmit,
                enabled = !viewModel.isLoading,
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

            if (viewModel.isSuccess) {
                Text(
                    text = texts.success,
                    color = Color(0xFF4CAF50), // Verde de éxito
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}