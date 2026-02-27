package com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.domain.models.Course
import com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses.util.CourseListStrings
// Importar Course y CourseListStrings deben estar disponibles en el proyecto

@Composable
fun CoursesList(
    viewModel: CoursesViewModel,
    texts: CourseListStrings,
    navigateToEnrollment: (Course) -> Unit
) {
    var courseToDelete by remember { mutableStateOf<Course?>(null) }
    val showDeleteDialog by remember { derivedStateOf { courseToDelete != null } }

    // 💡 NUEVO: Crear mapas de búsqueda de nombres para Materias y Profesores
    val subjectMap = viewModel.availableSubjects.associate { it.id to it.name }
    val teacherMap = viewModel.availableTeachers.associate { it.id to it.fullName }

    // 💡 Función de utilidad para obtener el nombre
    fun getSubjectName(subjectId: String): String {
        // Fallback a "Cargando..." si el Store aún no tiene la data o si hay un error.
        return subjectMap[subjectId] ?: "Cargando materia..."
    }

    // 💡 Función de utilidad para obtener el nombre completo
    fun getTeacherName(teacherId: String): String {
        // Fallback a "Cargando..." si el Store aún no tiene la data o si hay un error.
        return teacherMap[teacherId] ?: "Cargando profesor..."
    }

    if (viewModel.courses.isEmpty() && !viewModel.isLoading && viewModel.hasFetched) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(texts.emptyListMessage, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = viewModel::fetchCourses) {
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
        items(viewModel.courses, key = { it.id }) { course ->
            CourseListItem(
                course = course,
                subjectName = getSubjectName(course.subjectId),
                teacherName = getTeacherName(course.teacherId),
                onEdit = { viewModel.enterEditMode(course) },
                onDeleteRequest = { courseToDelete = course },
                onEnrollment = { navigateToEnrollment(course) },
                texts = texts
            )
        }
    }

    if (showDeleteDialog && courseToDelete != null) {
        val course = courseToDelete!!

        AlertDialog(
            onDismissRequest = { courseToDelete = null },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text(texts.deleteDialogTitle) },
            // Mensaje más descriptivo (idealmente deberías incluir el nombre del instituto aquí)
            text = { Text(texts.deleteDialogMessage("${course.academicYear} / ${course.group}")) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCourse(course.id)
                        courseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(texts.deleteConfirmAction)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { courseToDelete = null }) {
                    Text(texts.cancelAction)
                }
            }
        )
    }
}

@Composable
fun CourseListItem(
    course: Course,
    subjectName: String,
    teacherName: String,
    onEdit: () -> Unit,
    onDeleteRequest: () -> Unit,
    onEnrollment: () -> Unit,
    texts: CourseListStrings
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

                // ... (Contenido principal del curso)
                Text(
                    text = "${texts.groupPrefix} ${course.group} / ${texts.yearPrefix} ${course.academicYear}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$subjectName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${texts.teacherPrefix} $teacherName",
                    style = MaterialTheme.typography.bodyMedium,
                )
                // Métrica clave
                Text(
                    text = "👥 ${texts.studentsPrefix} ${course.enrolledStudentsCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // ----------------------------------------------------
            // ✅ ZONA DE ACCIONES HORIZONTALES
            // ----------------------------------------------------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentSize(Alignment.CenterEnd)
            ) {
                // 1. Botón de Matriculación/Enrollment (Navegación Primaria)
                IconButton(onClick = onEnrollment) {
                    Icon(
                        Icons.Filled.Group,
                        contentDescription = "Gestionar Alumnos",
                        // Usamos secondary para que se destaque como acción primaria
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                // 2. Botón de Menú de 3 Puntos (Otras Acciones)
                // Usamos Box para anclar el DropdownMenu a este IconButton
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Más Acciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 3. Dropdown Menu (Contiene Editar y Eliminar - Vertical)
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