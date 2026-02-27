package com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Quiz // Nuevo ícono para Quiz Templates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.domain.models.Subject
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.util.SubjectListStrings
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.subjects.util.openMarkdownFilePicker

@Composable
fun SubjectsList(
    viewModel: SubjectsViewModel,
    texts: SubjectListStrings,
    // << NUEVO: Función de navegación a Quiz Templates >>
    navigateToQuizTemplates: (subjectId: String, subjectName: String) -> Unit
) {
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    val showDeleteDialog by remember { derivedStateOf { subjectToDelete != null } }

    if (viewModel.subjects.isEmpty() && !viewModel.isListLoading && viewModel.hasFetched) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(texts.emptyListMessage, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = viewModel::fetchSubjects) {
                Text(texts.retryButton)
            }
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // O Adaptative(minSize = 300.dp)
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(viewModel.subjects, key = { it.id }) { subject ->
            SubjectListItem(
                subject = subject,
                // onEdit y onDeleteRequest ahora manejan el estado del formulario/diálogo
                onEdit = { viewModel.enterEditMode(subject) },
                onDeleteRequest = { subjectToDelete = subject },
                navigateToQuizTemplates = navigateToQuizTemplates, // << PASADO AL ITEM
                texts = texts,
                viewModel = viewModel,
            )
        }
    }

    // Diálogo de Confirmación de Eliminación
    if (showDeleteDialog && subjectToDelete != null) {
        val subject = subjectToDelete!!

        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text(texts.deleteDialogTitle) },
            text = { Text(texts.deleteDialogMessage(subject.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSubject(subject.id)
                        subjectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(texts.deleteConfirmAction)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { subjectToDelete = null }) {
                    Text(texts.cancelAction)
                }
            }
        )
    }
}

@Composable
fun SubjectListItem(
    subject: Subject,
    onEdit: () -> Unit,
    onDeleteRequest: () -> Unit,
    // << NUEVO: Callback de navegación >>
    navigateToQuizTemplates: (subjectId: String, subjectName: String) -> Unit,
    texts: SubjectListStrings,
    viewModel: SubjectsViewModel
) {
    var expanded by remember { mutableStateOf(false) } // Estado del Dropdown Menu

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenido principal de la materia
            Column(modifier = Modifier.weight(1f)) {
                Text(subject.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                subject.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    Spacer(Modifier.height(4.dp))
                    Text("${texts.descriptionPrefix} $desc", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }

            // Sección de Botones de Acción
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentSize(Alignment.CenterEnd)
            ) {
                // 1. Botón de Navegación a Quiz Templates
                IconButton(onClick = { navigateToQuizTemplates(subject.id, subject.name) }) {
                    Icon(
                        Icons.Filled.Quiz,
                        contentDescription = "Quiz Templates", // Usar un texto traducido si está disponible
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }

                IconButton(onClick = {
                    // Llamamos a una función que abra el selector de archivos
                    openMarkdownFilePicker { selectedFile ->
                        //viewModel.selectedSubject = subject // Marcamos cuál materia estamos editando
                        viewModel.saveMarkdownContent(selectedFile, subject.id, emptyList()) // Ejecutamos el Use Case
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Subir Contenido MD",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // 2. Menú de Más Acciones (Editar/Eliminar)
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = texts.moreActions, // Asumo que tienes 'moreActions'
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Dropdown Menu
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(IntrinsicSize.Max)
                    ) {
                        // Acción de Editar
                        DropdownMenuItem(
                            text = { Text(texts.editAction) },
                            onClick = {
                                onEdit()
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Edit, contentDescription = null)
                            }
                        )
                        // Acción de Eliminar (Destructiva)
                        DropdownMenuItem(
                            text = { Text(texts.deleteAction, color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                onDeleteRequest()
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        )
                    }
                }
            }
        }
    }
}