package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizQuestions.util.QuizQuestionFormStrings

@Composable
fun QuizQuestionForm(
    viewModel: QuizQuestionViewModel,
    texts: QuizQuestionFormStrings,
    templateName: String
) {
    val isEditing = viewModel.questionToEdit != null
    val title = if (isEditing) texts.titleEdit else texts.titleCreate
    val buttonIcon: ImageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Add
    val isFormEnabled = !viewModel.isFormSubmitting

    Surface(
        shadowElevation = 8.dp,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))

            // Campo de texto de la pregunta
            OutlinedTextField(
                value = viewModel.questionTextInput,
                onValueChange = { viewModel.questionTextInput = it },
                label = { Text(texts.textFieldLabel) },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormEnabled
            )

            Spacer(Modifier.height(16.dp))

            // Botones de presets rápidos
            Text(texts.quickFormatsTitle, style = MaterialTheme.typography.labelMedium)

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.setTrueFalsePreset() },
                    modifier = Modifier.weight(1f),
                    enabled = isFormEnabled
                ) {
                    // USANDO TRADUCCIÓN PARA V/F
                    Text(texts.presetTrueFalse)
                }
                OutlinedButton(
                    onClick = { viewModel.setFiveOptionsPreset() },
                    modifier = Modifier.weight(1f),
                    enabled = isFormEnabled
                ) {
                    // USANDO TRADUCCIÓN PARA 5 OPCIONES
                    Text(texts.presetFiveOptions)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(texts.optionsSectionTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            // --- AQUÍ EMPIEZA LA LISTA DONDE EXISTE "option" ---
            viewModel.optionsInput.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ahora sí, option viene del forEachIndexed
                    RadioButton(
                        selected = option.isCorrect,
                        onClick = { viewModel.toggleOptionCorrect(index) },
                        enabled = isFormEnabled
                    )

                    OutlinedTextField(
                        value = option.text,
                        onValueChange = { viewModel.updateOptionText(index, it) },
                        placeholder = { Text(texts.optionTextPlaceholder) },
                        modifier = Modifier.weight(1f),
                        enabled = isFormEnabled
                    )

                    IconButton(
                        onClick = { viewModel.removeOption(index) },
                        enabled = isFormEnabled && viewModel.optionsInput.size > 1
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            // --- AQUÍ TERMINA LA LISTA ---

            TextButton(onClick = viewModel::addOption, enabled = isFormEnabled) {
                Text("+ ${texts.addOptionButton}")
            }

            Spacer(Modifier.height(24.dp))

            // Botones de acción final
            PrimaryButton(
                onClick = viewModel::saveQuestion,
                enabled = isFormEnabled,
                modifier = Modifier.fillMaxWidth(),
                icon = { Icon(buttonIcon, contentDescription = null) },
                text = texts.buttonSave
            )

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.isFormOpen = false },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormEnabled
            ) {
                Text(texts.buttonCancel)
            }

            viewModel.errorMessage?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}