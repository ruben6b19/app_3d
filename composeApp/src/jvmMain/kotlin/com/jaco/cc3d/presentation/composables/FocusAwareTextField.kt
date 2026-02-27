package com.jaco.cc3d.presentation.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager // 💡 Importación necesaria para manejar el foco
import androidx.compose.ui.text.input.ImeAction

@Composable
fun FocusAwareTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    error: String?,
    focusRequester: FocusRequester,
    nextFocusRequester: FocusRequester? = null,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    // 💡 NUEVO: Acción a ejecutar si es el último campo (ImeAction.Done)

    // 💡 PARÁMETROS PARA CONTROLAR LÍNEAS
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    onDoneAction: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current // Captura el FocusManager

    // 💡 Lógica para manejar la acción "Done" del teclado si se proporciona onDoneAction.
    // Esto sobrescribe el 'onDone' proporcionado en 'keyboardActions' si onDoneAction está presente
    // y el campo está configurado para ser el último (ImeAction.Done).
    val finalKeyboardActions = if (onDoneAction != null && imeAction == ImeAction.Done) {
        KeyboardActions(
            onDone = {
                focusManager.clearFocus() // Ocultar teclado/limpiar foco antes de enviar
                onDoneAction() // Ejecutar la acción final de envío
            },
            // Aseguramos que otras acciones (onNext, etc.) sigan usando las originales
            onNext = keyboardActions.onNext,
            onPrevious = keyboardActions.onPrevious,
            onSearch = keyboardActions.onSearch,
            onGo = keyboardActions.onGo,
            onSend = keyboardActions.onSend
        )
    } else {
        // Si no es el último campo o no se proporciona una acción final, usar las acciones originales
        keyboardActions
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        // 💡 Usamos los parámetros dinámicos
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .focusProperties {
                nextFocusRequester?.let { next = it }
            },
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = finalKeyboardActions, // Aplicamos las acciones finales
        enabled = enabled
    )
}