// com/jaco/cc3d/presentation/privado/users/UsersList.kt

package com.jaco.cc3d.presentation.privado.privateDashboardScreen.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.users.util.UserListStrings

// 💡 Mapeos para mostrar los valores numéricos de Rol y Estado
val RoleDisplay = mapOf(
    0 to "Estudiante",
    1 to "Profesor",
    2 to "Administrador"
)
val StatusDisplay = mapOf(
    0 to "Inactivo",
    1 to "Activo",
    2 to "Bloqueado"
)


// ----------------------------------------------------
// 1. READ: Composable para listar los usuarios (M3)
// ----------------------------------------------------

@Composable
fun UsersList(viewModel: UsersViewModel, texts: UserListStrings) {
    // 💡 Estado para gestionar el diálogo de confirmación de eliminación
    var userToDelete by remember { mutableStateOf<User?>(null) }
    val showDeleteDialog by remember { derivedStateOf { userToDelete != null } }

    // Manejo de lista vacía o error de carga
    if (viewModel.users.isEmpty() && !viewModel.isLoading && viewModel.hasFetched) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(texts.emptyListMessage, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = viewModel::fetchUsers) {
                Text(texts.retryButton)
            }
        }
        return
    }

    // Lista principal
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(viewModel.users, key = { it.id }) { user ->
            UserListItem(
                user = user,
                onEdit = { viewModel.enterEditMode(user) },
                onDeleteRequest = { userToDelete = user }, // Establece el usuario a eliminar
                texts = texts
            )
        }
    }

    // 💡 Diálogo de Confirmación de Eliminación (similar a InstitutesList)
    if (showDeleteDialog && userToDelete != null) {
        val user = userToDelete!!

        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text(texts.deleteDialogTitle) },
            text = { Text(texts.deleteDialogMessage(user.fullName)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(user.id) // Llama a la acción del ViewModel
                        userToDelete = null // Cierra el diálogo
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(texts.deleteAction)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { userToDelete = null }
                ) {
                    Text(texts.cancelAction)
                }
            }
        )
    }
}


// ----------------------------------------------------
// Composable para cada elemento de la lista
// ----------------------------------------------------

@Composable
fun UserListItem(
    user: User,
    onEdit: () -> Unit,
    onDeleteRequest: () -> Unit,
    texts: UserListStrings
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit), // Usar clic en toda la tarjeta para edición (o solo para ver detalles)
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))

                // Email
                Text(user.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))

                // Roles (convertir la lista de Ints a nombres legibles)
                val roles = user.role.mapNotNull { RoleDisplay[it] }.joinToString(", ")
                Text("${texts.rolePrefix} $roles", style = MaterialTheme.typography.bodySmall)

                // Estado
                val status = StatusDisplay[user.status] ?: "N/A"
                Text("${texts.statusPrefix} $status", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(Modifier.width(16.dp))

            // Botones de acción
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = texts.editAction, tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDeleteRequest) {
                Icon(Icons.Filled.Delete, contentDescription = texts.deleteAction, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}