package com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
// 💡 Importar para el estado del diálogo
import androidx.compose.runtime.* import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.domain.models.Institute
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.CityOptions
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.InstituteListStrings
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.institutes.util.LanguageOptions
import com.jaco.cc3d.utils.formatIsoDateToDdMmYyyy

// ----------------------------------------------------
// 1. READ: Composable para listar los institutos (M3)
// ----------------------------------------------------

@Composable
fun InstitutesList(
    viewModel: InstitutesViewModel,
    texts: InstituteListStrings,
    navigateToUsers: (String) -> Unit,
    navigateToCourses: (String, String) -> Unit) {
    // 💡 Estado para gestionar el diálogo de confirmación de eliminación
    var instituteToDelete by remember { mutableStateOf<Institute?>(null) }
    val showDeleteDialog by remember { derivedStateOf { instituteToDelete != null } }

    if (viewModel.institutes.isEmpty() && !viewModel.isLoading && viewModel.hasFetched) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(texts.emptyListMessage, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = viewModel::fetchInstitutes) {
                Text(texts.retryButton)
            }
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(viewModel.institutes, key = { it.id }) { institute ->
            InstituteListItem(
                institute = institute,
                onEdit = { viewModel.enterEditMode(institute) },
                // 💡 Al hacer clic en eliminar, guardamos el instituto para abrir el diálogo
                onDeleteRequest = { instituteToDelete = institute },
                navigateToUsers = navigateToUsers,
                navigateToCourses = navigateToCourses,
                texts = texts
            )
        }
    }

    // 💡 Diálogo de Confirmación de Eliminación
    if (showDeleteDialog && instituteToDelete != null) {
        val institute = instituteToDelete!! // Uso seguro porque showDeleteDialog es true

        AlertDialog(
            onDismissRequest = {
                instituteToDelete = null
            },
            icon = { Icon(Icons.Filled.Delete, contentDescription = texts.deleteDialogTitle) },
            // 💡 Título traducido
            title = { Text(texts.deleteDialogTitle) },
            text = {
                // 💡 Mensaje traducido con el nombre del instituto
                Text(texts.deleteDialogMessage(institute.name))
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteInstitute(institute.id)
                        instituteToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    // 💡 Botón de confirmación traducido
                    Text(texts.deleteAction)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { instituteToDelete = null }
                ) {
                    // 💡 Botón de cancelación traducido
                    Text(texts.cancelAction)
                }
            }
        )
    }
}

// Mantenemos el nombre del ítem de la lista para claridad
@Composable
fun InstituteListItem(
    institute: Institute,
    onEdit: () -> Unit,
    onDeleteRequest: () -> Unit,
    navigateToUsers: (String) -> Unit,
    navigateToCourses: (String, String) -> Unit,
    texts: InstituteListStrings
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Nombre del Instituto
                Text(institute.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))

                // Fecha y Ubicación
                val formattedDate = formatIsoDateToDdMmYyyy(institute.foundationDate)
                Text("${texts.foundationPrefix} $formattedDate", style = MaterialTheme.typography.bodyMedium)
                Text("${texts.cityPrefix} ${CityOptions[institute.city.toString()]} | ${texts.languagePrefix} ${LanguageOptions[institute.language]}",
                    style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(Modifier.height(8.dp)) // Espacio antes de las métricas

                // 🆕 Fila para mostrar Conteo de Usuarios y Cursos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.School,
                        contentDescription = texts.coursesAction,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        // Asumimos que institute.coursesCount existe
                        "${institute.coursesCount} ${texts.coursesLabel}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.width(16.dp))
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = texts.usersAction,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        // Asumimos que institute.usersCount existe
                        "${institute.usersCount} ${texts.usersLabel}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                // ----------------------------------------------------
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentSize(Alignment.CenterEnd)
            ) {
                // 1. Botón de Navegación a Cursos
                IconButton(onClick = { navigateToCourses(institute.id, institute.name) }) {
                    Icon(Icons.Filled.School, contentDescription = texts.coursesAction, tint = MaterialTheme.colorScheme.primary)
                }

                // 2. Botón de Navegación a Usuarios
                IconButton(onClick = { navigateToUsers(institute.id) }) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = texts.usersAction, tint = MaterialTheme.colorScheme.secondary)
                }

                // 3. Botón de Menú de 3 Puntos (Más Acciones)
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Más Acciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 4. Dropdown Menu
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