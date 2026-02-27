package com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.domain.models.ScheduledQuiz
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util.QuizStrings

@Composable
fun QuizSelectionDialog(
    availableQuizzes: List<ScheduledQuiz>,
    strings: QuizStrings,
    onDismiss: () -> Unit,
    onQuizSelected: (ScheduledQuiz) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(strings.title)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (availableQuizzes.isEmpty()) {
                    Text(
                        text = strings.emptyList,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    availableQuizzes.forEach { quiz ->
                        val attempt = quiz.userAttempt
                        // El examen está finalizado si status es 1
                        val isFinished = attempt?.status == 1
                        val isInProgress = attempt?.status == 0

                        Card(
                            // Si ya terminó, no permitimos volver a entrar
                            onClick = { if (!isFinished) onQuizSelected(quiz) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            enabled = !isFinished, // Deshabilita visualmente si terminó
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isFinished -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    isInProgress -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icono dinámico según el estado
                                Icon(
                                    imageVector = when {
                                        isFinished -> Icons.Default.CheckCircle
                                        isInProgress -> Icons.Default.PlayCircle
                                        else -> Icons.AutoMirrored.Filled.Assignment
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = when {
                                        isFinished -> Color(0xFF66BF3C) // Verde Quizizz
                                        isInProgress -> Color(0xFFEAA43A) // Naranja
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                )

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = quiz.quizTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isFinished) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.onSurface
                                    )

                                    // Subtexto con información del intento
                                    if (attempt?.hasAttempted == true) {
                                        val statusText = if (isFinished) "Completado - Nota: ${attempt.score}" else "En curso"
                                        Text(
                                            text = statusText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isFinished) Color(0xFF66BF3C) else Color(0xFFEAA43A)
                                        )
                                    }
                                }

                                if (!isFinished) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.NavigateNext,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.close) }
        }
    )
}