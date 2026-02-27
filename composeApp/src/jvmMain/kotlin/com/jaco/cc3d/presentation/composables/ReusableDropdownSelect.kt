package com.jaco.cc3d.presentation.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableDropdownSelect(
    labelText: String,
    selectedCode: String,
    options: Map<String, String>,
    onCodeSelected: (String) -> Unit,
    errorText: String,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    enabled: Boolean = true,
    noItemsMessage: String = "No options available",
    onSelectionDone: (() -> Unit)? = null, // Acción a ejecutar si es el último campo
) {
    // Estado local para el menú desplegable
    var isExpanded by remember { mutableStateOf(false) }

    // Determina el nombre visible a partir del código, o un valor por defecto.
    //val selectedName = options[selectedCode] ?: labelText
    val selectedName = if (options.isEmpty()) {
        noItemsMessage
    } else {
        options[selectedCode] ?: labelText
    }

    // 💡 Lógica Centralizada de Avance o Finalización
    val performNextAction: () -> Unit = {
        if (onSelectionDone != null) {
            onSelectionDone() // Si se define onSelectionDone (es el último campo), enviar formulario
        } else {
            focusManager.moveFocus(FocusDirection.Down) // Si no, ir al siguiente campo
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text(labelText) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                enabled = enabled,
                // --- Manejo de Errores ---
                isError = errorText.isNotEmpty(),
                supportingText = if (errorText.isNotEmpty()) {
                    { Text(errorText, color = MaterialTheme.colorScheme.error) }
                } else null,

                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),

                // --- Manejo del Teclado y Foco ---
                keyboardOptions = KeyboardOptions(
                    // Usamos Done si es el último campo, o Next si va a otro campo
                    imeAction = if (onSelectionDone != null) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { performNextAction() },
                    onDone = { performNextAction() } // Se ejecuta al presionar Done/Enter
                )
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                options.forEach { (code, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onCodeSelected(code)
                            isExpanded = false
                            // 💡 Ejecutar la acción de avance/finalización después de la selección
                            //performNextAction()
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}