package com.jaco.cc3d.presentation.privado.studentDashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.domain.models.Enrollment
import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.studentDashboard.util.StudentDashboardResources
import com.jaco.cc3d.utils.formatIsoDateToDdMmYyyy

@Composable
fun StudentDashboardScreen(
    viewModel: StudentDashboardViewModel,
    onLogout: () -> Unit,
    onMenuClick: () -> Unit,
    onCourseClick: (courseId: String) -> Unit
) {

    val languageActions = LocalLanguageActions.current
    val currentLangCode = languageActions.currentLanguage
    val snackbarHostState = remember { SnackbarHostState() } //
    val resources = StudentDashboardResources.get(viewModel.lang)

    LaunchedEffect(currentLangCode) {
        viewModel.lang = currentLangCode
    }



    LaunchedEffect(viewModel.errorMessage, viewModel.mustLogout) {
        // Manejo de la sesión expirada
        if (viewModel.mustLogout) {
            viewModel.onLogoutHandled()
            onLogout()
            return@LaunchedEffect
        }

        // Manejo de Errores
        viewModel.errorMessage?.let { message ->
            // Muestra el mensaje de error específico del backend
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            // Es vital limpiar el mensaje después de mostrarlo para que no se muestre de nuevo
            // NOTA: Para limpiar el mensaje, necesitamos una función en el ViewModel
            viewModel.clearErrorMessage() // Necesitas agregar esta función
        }
    }

    ScreenLayout(
        title = resources.welcome.titleScreen,
        //onMenuClick = onMenuClick
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 56.dp, bottom = 16.dp, start = 16.dp, end = 16.dp   )) {
            // Bienvenida
            Text(
                text = resources.welcome.greeting(viewModel.studentName),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = resources.welcome.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.enrollments.isEmpty()) {
                EmptyState(
                    message = resources.list.emptyMessage,
                    buttonText = resources.list.retryButton,
                    onRetry = { viewModel.fetchMyEnrollments() }
                )
            } else {
                // Cuadrícula de Cursos
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    items(viewModel.enrollments) { enrollment ->
                        //val subjectName = viewModel.getSubjectName(enrollment.sub)
                        CourseCard(
                            enrollment = enrollment,
                            isDownloaded = viewModel.downloadedSubjects.contains(enrollment.subjectId),
                            enrolledOnText = resources.list.enrolledOn, // 💡 Pasar prefijo
                            onClick = { onCourseClick(enrollment.courseId) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCard(
    enrollment: Enrollment,
    isDownloaded: Boolean,
    enrolledOnText: String,
    onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            // 1. Icono de descarga (se dibuja encima en la esquina)
            if (isDownloaded) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Lo envía a la esquina superior derecha
                        .padding(12.dp)          // Espaciado interno desde el borde de la card
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono representativo

                Icon(
                    Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Título de la Materia
                    Text(
                        text = enrollment.subjectName ?: "Materia sin nombre",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    // Fecha de inscripción
                    Text(
                        text = "$enrolledOnText ${formatIsoDateToDdMmYyyy(enrollment.enrollmentDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    // Fila de etiquetas (Grupo y Año)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // --- ETIQUETA DE GRUPO ---
                        // Usamos Surface en lugar de Badge para tener control total del borde
                        Surface(
                            color = MaterialTheme.colorScheme.secondary, // Color sólido para contraste
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = enrollment.group,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        // --- ETIQUETA DE AÑO ---
                        Surface(
                            color = MaterialTheme.colorScheme.error, // Color sólido contrastante
                            contentColor = MaterialTheme.colorScheme.onError,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = enrollment.academicYear.toString(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    buttonText: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(buttonText)
        }
    }
}