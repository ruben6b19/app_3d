package com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jaco.cc3d.domain.models.QuizQuestion
import com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util.QuizStrings

val QuizizzPurple = androidx.compose.ui.graphics.Color(0xFF8854C0)
val QuizizzBlue = androidx.compose.ui.graphics.Color(0xFF2D70AE)
val QuizizzRed = androidx.compose.ui.graphics.Color(0xFFD61C4E)
val QuizizzYellow = androidx.compose.ui.graphics.Color(0xFFEAA43A)
val QuizizzGreen = androidx.compose.ui.graphics.Color(0xFF66BF3C) // Verde éxito

val quizizzColors = listOf(QuizizzPurple, QuizizzBlue, QuizizzRed, QuizizzYellow)

@Composable
fun QuizDialog(
    questions: List<QuizQuestion>,
    strings: QuizStrings,
    onDismiss: () -> Unit,
    onSubmit: (Map<String, Int>) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val selectedAnswers = remember { mutableStateMapOf<String, Int>() }

    var showConfirmPopUp by remember { mutableStateOf(false) }
    var pendingIndex by remember { mutableIntStateOf(-1) }

    val currentQuestion = questions.getOrNull(currentIndex)
    val isLastQuestion = currentIndex == questions.size - 1

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (showConfirmPopUp && currentQuestion != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmPopUp = false
                pendingIndex = -1
            },
            title = { Text("¿Confirmar respuesta?") },
            text = { Text("Una vez confirmada, pasarás a la siguiente pregunta.") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedAnswers[currentQuestion.id] = pendingIndex
                        showConfirmPopUp = false
                        if (isLastQuestion) onSubmit(selectedAnswers) else {
                            currentIndex++
                            pendingIndex = -1 // Importante resetear aquí
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = QuizizzGreen)
                ) { Text("Confirmar", color = androidx.compose.ui.graphics.Color.White) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmPopUp = false
                    pendingIndex = -1
                }) { Text("Cancelar") }
            }
        )
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Indicador de progreso arriba
                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / questions.size },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).height(10.dp),
                    color = QuizizzPurple,
                    trackColor = QuizizzPurple.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = strings.questionCounter(currentIndex + 1, questions.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = QuizizzPurple
                )

                if (currentQuestion != null) {
                    // Usamos una Column con verticalScroll para que las opciones no se corten
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()) // Permite scroll si hay muchas opciones
                    ) {
                        Text(
                            text = currentQuestion.questionText,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )

                        currentQuestion.options.forEachIndexed { index, option ->
                            val isThisPending = pendingIndex == index
                            val someoneIsPending = pendingIndex != -1
                            val baseColor = quizizzColors.getOrElse(index % quizizzColors.size) { QuizizzPurple }

                            val targetColor = if (isThisPending) QuizizzGreen else baseColor
                            val alpha = if (someoneIsPending && !isThisPending) 0.3f else 1f

                            Card(
                                onClick = {
                                    pendingIndex = index
                                    showConfirmPopUp = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth() // Solo fillMaxWidth, NO fillMaxHeight
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = targetColor.copy(alpha = alpha)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isThisPending) 8.dp else 2.dp)
                            ) {
                                // Aquí estaba el error, la Row debe tener un padding pero no fillMaxSize
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${(index + 65).toChar()}",
                                                color = androidx.compose.ui.graphics.Color.White,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(16.dp))

                                    Text(
                                        text = option.text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
                        }
                        // Espacio extra al final del scroll
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}