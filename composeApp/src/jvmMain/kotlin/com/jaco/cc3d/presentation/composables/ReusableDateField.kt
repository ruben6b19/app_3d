package com.jaco.cc3d.presentation.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun ReusableDateField(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    error: String?,
    focusRequester: FocusRequester,
    nextFocusRequester: FocusRequester? = null,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val maxInputLength = 10 // DD/MM/YYYY

    // 1. CORRECCIÓN DE ESTADO:
    // Quitamos 'value' del remember. Ahora el estado interno persiste aunque el VM se actualice,
    // permitiendo que el cursor se mueva libremente.
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    // 2. SINCRONIZACIÓN EXTERNA:
    // Si el valor cambia desde FUERA (ej. al cargar datos para editar), actualizamos el estado.
    // La condición 'value != textFieldValueState.text' evita bucles infinitos al escribir.
    if (value != textFieldValueState.text) {
        textFieldValueState = textFieldValueState.copy(text = value, selection = TextRange(value.length))
    }

    val onValidatedValueChange: (TextFieldValue) -> Unit = { newValue ->
        val newText = newValue.text
        val filteredText = newText.filter { it.isDigit() || it == '/' }

        if (filteredText.length <= maxInputLength) {

            // 3. CORRECCIÓN DEL FOCO (Lógica inteligente):
            // Solo saltamos si la longitud ANTES era menor a 10 y AHORA es 10.
            // Esto permite editar una fecha completa sin que salte el foco.
            val isJustCompleted = textFieldValueState.text.length < maxInputLength &&
                    filteredText.length == maxInputLength

            // Actualizamos el estado interno con la nueva posición del cursor (newValue.selection)
            textFieldValueState = newValue.copy(text = filteredText)

            // Emitimos al ViewModel
            if (filteredText != value) {
                onValueChange(filteredText)
            }

            // Movemos el foco solo si acabamos de "terminar" de escribir
            if (isJustCompleted && imeAction == ImeAction.Next) {
                nextFocusRequester?.requestFocus()
            }
        }
    }

    OutlinedTextField(
        value = textFieldValueState,
        onValueChange = onValidatedValueChange,
        label = { Text(labelText) },
        singleLine = true,
        isError = error != null,
        supportingText = {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .focusProperties {
                nextFocusRequester?.let { next = it }
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone, // Phone suele ser mejor para números y símbolos '/'
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        enabled = enabled
    )
}