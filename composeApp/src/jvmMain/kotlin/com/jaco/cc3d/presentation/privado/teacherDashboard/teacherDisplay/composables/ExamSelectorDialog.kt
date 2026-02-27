package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.TeacherDisplayViewModel
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.DisplayControlStrings
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jaco.cc3d.domain.models.QuizTemplate

@Composable
fun ExamSelectorDialog(
    courseId: String,
    onDismiss: () -> Unit,
    texts: DisplayControlStrings,
    viewModel: TeacherDisplayViewModel
) {
    // 💡 Estado para controlar qué examen se quiere programar
    var selectedQuiz by remember { mutableStateOf<QuizTemplate?>(null) }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (selectedQuiz != null) {
        AlertDialog(
            onDismissRequest = { selectedQuiz = null },
            title = { Text("Confirmar Programación") },
            text = { Text("¿Estás seguro de que deseas programar el examen '${selectedQuiz?.name}' para este curso?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val quiz = selectedQuiz!!
                        viewModel.scheduleNewQuiz(
                            courseId = courseId,
                            quizTemplateId = quiz.id,
                            date = java.time.Instant.now().toString(),
                            details = "Programado desde el panel de control"
                        ) {
                            selectedQuiz = null
                            onDismiss()
                        }
                    },
                    enabled = !viewModel.isSchedulingQuiz
                ) {
                    if (viewModel.isSchedulingQuiz) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Confirmar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedQuiz = null }, enabled = !viewModel.isSchedulingQuiz) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DIÁLOGO PRINCIPAL (LISTA) ---
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(texts.examTooltip) },
        text = {
            Box(modifier = Modifier.heightIn(max = 400.dp)) {
                if (viewModel.isLoadingQuizzes) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (viewModel.availableQuizzes.isEmpty()) {
                    Text("No hay exámenes disponibles.")
                } else {
                    LazyColumn {
                        items(viewModel.availableQuizzes) { quiz ->
                            val alreadyScheduled = quiz.isAlreadyScheduled

                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = quiz.name,
                                        color = if (alreadyScheduled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                supportingContent = {
                                    if (alreadyScheduled) {
                                        Text("Ya programado en este curso", color = MaterialTheme.colorScheme.error)
                                    } else {
                                        Text("Idioma: ${quiz.language}")
                                    }
                                },
                                trailingContent = {
                                    if (alreadyScheduled) {
                                        Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                modifier = Modifier.clickable(
                                    enabled = !alreadyScheduled && !viewModel.isSchedulingQuiz
                                ) {
                                    // 💡 En lugar de programar directo, abrimos la confirmación
                                    selectedQuiz = quiz
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, enabled = !viewModel.isSchedulingQuiz) {
                Text("Cerrar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}