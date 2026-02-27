package com.jaco.cc3d.presentation.privado.teacherDashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Importaciones de tu proyecto (ajusta los paquetes según tu estructura real)
import com.jaco.cc3d.LocalLanguageActions
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.presentation.composables.ScreenLayout
import com.jaco.cc3d.presentation.privado.studentDashboard.EmptyState // Reutilizando el que ya tienes
import com.jaco.cc3d.presentation.privado.teacherDashboard.util.TeacherCourseListStrings
import com.jaco.cc3d.presentation.privado.teacherDashboard.util.TeacherDashboardResources

@Composable
fun TeacherDashboardScreen(
    viewModel: TeacherDashboardViewModel,
    onLogout: () -> Unit,
    onCourseClick: (courseId: String) -> Unit
) {
    val languageActions = LocalLanguageActions.current
    val resources = TeacherDashboardResources.get(languageActions.currentLanguage)
    val snackbarHostState = remember { SnackbarHostState() } //

    LaunchedEffect(languageActions.currentLanguage) {
        viewModel.lang = languageActions.currentLanguage
    }

    LaunchedEffect(viewModel.mustLogout) {
        // Manejo de la sesión expirada
        println("mustlogout")
        if (viewModel.mustLogout) {
            println(" mustLogout detectado como TRUE. Iniciando salida...")
            viewModel.onLogoutHandled()
            onLogout()
        }
    }

    LaunchedEffect(viewModel.errorMessage, viewModel.mustLogout) {
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


    ScreenLayout(title = resources.welcome.titleScreen) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 56.dp, bottom = 16.dp, start = 16.dp, end = 16.dp )) {
            Text(
                text = resources.welcome.greeting(viewModel.teacherName),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = resources.welcome.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else if (viewModel.courses.isEmpty()) {
                EmptyState(
                    message = resources.list.emptyMessage,
                    buttonText = resources.list.retryButton,
                    onRetry = { viewModel.fetchMyCourses() }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.courses) { course ->
                        TeacherCourseCard(
                            course = course,
                            isDownloaded = viewModel.downloadedSubjects.contains(course.subjectId),
                            resources = resources.list,
                            onClick = { onCourseClick(course.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherCourseCard(
    course: Course,
    isDownloaded: Boolean,
    resources: TeacherCourseListStrings,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // 🎯 Envolvemos todo en un Box para poder posicionar el icono en la esquina
        Box(modifier = Modifier.fillMaxWidth()) {

            // 1. EL ICONO DE DESCARGA (Se dibuja encima en la esquina)
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

            // 2. EL CONTENIDO DE LA CARD (Tu Row original)
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.Person,
                    null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )


                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.subjectName ?: resources.unknownSubject,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.padding(end = 24.dp) // 🎯 Espacio extra para que el texto no tape el icono
                    )

                    Text(
                        text = resources.studentsCount(course.enrolledStudentsCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary, // Color sólido para contraste
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = course.group,
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
                                text = course.academicYear.toString(),
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