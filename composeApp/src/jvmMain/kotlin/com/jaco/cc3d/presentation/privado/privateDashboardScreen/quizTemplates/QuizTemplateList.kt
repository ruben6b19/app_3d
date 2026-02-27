package com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QuestionAnswer // Usamos QuestionAnswer o similar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.domain.models.QuizTemplate
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.quizTemplates.util.QuizTemplateListStrings

@Composable
fun QuizTemplateList(
    viewModel: QuizTemplateViewModel,
    texts: QuizTemplateListStrings,
    // 💡 NUEVO: Callback para navegar a la gestión de preguntas
    onNavigateToQuestions: (QuizTemplate) -> Unit
) {
    // ... (Diálogo de confirmación de eliminación se mantiene igual)
    var templateToRemove by remember { mutableStateOf<QuizTemplate?>(null) }

    // Diálogo de confirmación
    templateToRemove?.let { template ->
        AlertDialog(
            onDismissRequest = { templateToRemove = null },
            title = { Text(texts.deleteDialogTitle) },
            text = { Text(texts.deleteDialogMessage(template.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteQuizTemplate(template.id)
                        templateToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(texts.deleteConfirmAction)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { templateToRemove = null }) {
                    Text(texts.cancelAction)
                }
            }
        )
    }

    // Contenido Principal de la Lista
    if (viewModel.quizTemplates.isEmpty() && !viewModel.isListLoading) {
        // Mensaje de lista vacía
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(texts.emptyListMessage, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = viewModel::loadQuizTemplates) {
                Text(texts.retryButton)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // O Adaptative(minSize = 300.dp)
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        )  {
            items(viewModel.quizTemplates, key = { it.id }) { template ->
                QuizTemplateCard(
                    template = template,
                    texts = texts,
                    onEditRequest = { viewModel.openEditForm(template) },
                    onDeleteRequest = { templateToRemove = template },
                    onQuestionsRequest = { onNavigateToQuestions(template) }, // 💡 NUEVA ACCIÓN
                    isBeingDeleted = viewModel.templateIdBeingDeleted == template.id
                )
            }
        }
    }
}

// Composable individual para mostrar los detalles de la plantilla
@Composable
fun QuizTemplateCard(
    template: QuizTemplate,
    texts: QuizTemplateListStrings,
    onEditRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
    onQuestionsRequest: (QuizTemplate) -> Unit,
    isBeingDeleted: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sección de Contenido (Izquierda)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = template.name, // Quitamos el prefijo para que el Badge luzca mejor al lado
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.width(8.dp))

                    // INDICADOR DE IDIOMA (Badge)
                    Surface(
                        color = if (template.language == "es")
                            MaterialTheme.colorScheme.tertiaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = template.language.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (template.language == "es")
                                MaterialTheme.colorScheme.onTertiaryContainer
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Texto secundario con el idioma completo (Opcional)
                Text(
                    text = "${texts.languagePrefix} ${if (template.language == "es") "Español" else "English"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sección de Acciones (Derecha)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Botón de Preguntas
                IconButton(onClick = { onQuestionsRequest(template) }, enabled = !isBeingDeleted) {
                    Icon(
                        Icons.Filled.QuestionAnswer,
                        contentDescription = texts.questionsAction,
                        tint = MaterialTheme.colorScheme.primary // Cambiado a primary para resaltar
                    )
                }

                // Menú de 3 Puntos
                Box {
                    IconButton(onClick = { expanded = true }, enabled = !isBeingDeleted) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = texts.moreActions,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(texts.editAction) },
                            onClick = { onEditRequest(); expanded = false },
                            leadingIcon = { Icon(Icons.Filled.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(texts.deleteAction, color = MaterialTheme.colorScheme.error) },
                            onClick = { onDeleteRequest(); expanded = false },
                            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }
        }
    }
}