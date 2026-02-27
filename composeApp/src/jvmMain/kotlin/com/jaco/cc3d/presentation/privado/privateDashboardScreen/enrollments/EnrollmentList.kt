package com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.domain.models.Enrollment
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments.util.EnrollmentListStrings
import com.jaco.cc3d.utils.formatIsoDateToDdMmYyyy // Asumiendo que existe

@Composable
fun EnrollmentList(
    viewModel: EnrollmentViewModel,
    texts: EnrollmentListStrings
) {
    var enrollmentToRemove by remember { mutableStateOf<Enrollment?>(null) }

    // Función auxiliar para obtener nombre del estudiante desde el ViewModel/Store
    fun getStudentName(studentId: String): String {
        return viewModel.getStudentNameById(studentId) // Implementar en VM
    }

    fun getStudentEmail(studentId: String): String {
        return viewModel.getStudentEmailById(studentId) // Implementar en VM
    }

    if (viewModel.enrollments.isEmpty() && !viewModel.isEnrollmentListLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(texts.emptyListMessage, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                Button(onClick = viewModel::fetchEnrollments) {
                    Text(texts.retryButton)
                }
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
    )  {
        items(viewModel.enrollments, key = { it.id }) { enrollment ->
            val studentName = getStudentName(enrollment.studentId)
            EnrollmentListItem(
                enrollment = enrollment,
                studentName = studentName,
                studentEmail = getStudentEmail(enrollment.studentId),
                onRemoveRequest = { enrollmentToRemove = enrollment },
                texts = texts,
                isBeingDeleted = viewModel.enrollmentIdBeingDeleted == enrollment.id
            )
        }
    }

    // Diálogo de confirmación
    if (enrollmentToRemove != null) {
        val enrollment = enrollmentToRemove!!
        val studentName = getStudentName(enrollment.studentId)

        AlertDialog(
            onDismissRequest = { enrollmentToRemove = null },
            icon = { Icon(Icons.Filled.PersonRemove, contentDescription = null) },
            title = { Text(texts.removeDialogTitle) },
            text = { Text(texts.removeDialogMessage(studentName)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEnrollment(enrollment.id)
                        enrollmentToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(texts.removeConfirmAction)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { enrollmentToRemove = null }) {
                    Text(texts.cancelAction)
                }
            }
        )
    }
}

@Composable
fun EnrollmentListItem(
    enrollment: Enrollment,
    studentName: String,
    studentEmail: String,
    onRemoveRequest: () -> Unit,
    texts: EnrollmentListStrings,
    isBeingDeleted: Boolean
) {
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
                Text(
                    text = "${texts.studentNamePrefix} $studentName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${texts.studentEmailPrefix} $studentEmail",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(4.dp))
                val date = formatIsoDateToDdMmYyyy(enrollment.enrollmentDate)
                Text(
                    text = "${texts.enrollmentDatePrefix} $date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = onRemoveRequest) {
                if (isBeingDeleted) {
                    // Si se está eliminando, mostramos un spinner pequeño
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    // Si no, mostramos el ícono de eliminar
                    Icon(
                        Icons.Filled.PersonRemove,
                        contentDescription = texts.removeAction,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}