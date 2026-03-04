package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaco.cc3d.domain.models.ScheduledQuiz
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.mainContentFontSizeState



@Composable
fun CustomEmptyOverlay(
    users: List<com.jaco.cc3d.data.network.Connection>, // Recibe la lista aquí
    quiz: ScheduledQuiz?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val globalFontSize by mainContentFontSizeState
    var selectedTab by remember { mutableStateOf(0) }

    val quizQuestions = quiz?.questions ?: emptyList()
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }

    Box(modifier = modifier.padding(8.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(1f) // Al ser una sola columna por pestaña, podemos reducir el ancho total
                .align(Alignment.CenterEnd),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // --- 2. SELECTOR DE PESTAÑAS (TABS) ---
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    TabButton(
                        text = "Participantes (${users.size})",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Quiz",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    if (selectedTab == 0) {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            // Título de la ventana
                            Text(
                                text = "Usuarios Conectados (${users.size})",

                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            if (users.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        "No hay nadie conectado",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(users) { connection ->
                                        UserItem(connection)
                                    }
                                }
                            }
                        }
                    } else {

                        if (quiz == null) {
                            // ESTADO VACÍO: Cuando no hay examen programado
                            EmptyQuizPlaceholder()
                        } else {
                            QuizReviewContent(
                                quiz = quiz,
                                questions = quizQuestions,
                                currentIndex = currentQuestionIndex,
                                showAnswer = showAnswer,
                                fontSize = globalFontSize,
                                onToggleAnswer = { showAnswer = !showAnswer },
                                onNext = {
                                    currentQuestionIndex = (currentQuestionIndex + 1) % quizQuestions.size
                                    showAnswer = false
                                },
                                onPrevious = {
                                    // Lógica para ir atrás sin salirnos del rango (vuelve al final si es menor a 0)
                                    currentQuestionIndex = if (currentQuestionIndex > 0) currentQuestionIndex - 1 else quizQuestions.size - 1
                                    showAnswer = false
                                }
                            )
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Cerrar",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (isSelected) Color.Transparent else Color.Black.copy(alpha = 0.05f)
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray

    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = text, color = contentColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            if (isSelected) {
                Box(Modifier.width(40.dp).height(3.dp).background(contentColor, CircleShape))
            }
        }
    }
}

@Composable
fun EmptyQuizPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.EditNote, null, Modifier.size(64.dp), tint = Color.LightGray)
            Text("No hay examen activo", color = Color.Gray)
        }
    }
}

@Composable
fun QuizReviewContent(
    quiz: ScheduledQuiz,
    questions: List<com.jaco.cc3d.domain.models.QuizQuestion>,
    currentIndex: Int,
    showAnswer: Boolean,
    fontSize: Float,
    onToggleAnswer: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = quiz.quizTitle,
            fontSize = (fontSize * 1.2f).sp,
            lineHeight = (fontSize * 1.4f).sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        if (questions.isNotEmpty()) {
            val q = questions[currentIndex]

            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio disponible
                    .fillMaxWidth()
                    .verticalScroll(scrollState) // Habilita el scroll
                    .padding(end = 8.dp) // Espacio para que el scroll no tape el contenido
            ) {
                Text(
                    text = "Pregunta ${currentIndex + 1}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = (fontSize * 0.7f).sp
                )
                Text(
                    text = q.questionText,
                    fontSize = fontSize.sp,
                    lineHeight = (fontSize * 1.3f).sp,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(Modifier.height(8.dp))

                // Lista de Opciones
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    q.options.forEach { option ->
                        QuizOptionItem(
                            option = option,
                            showResult = showAnswer,
                            fontSize = fontSize
                        )
                    }
                }

                // Un pequeño padding extra al final del scroll para que la última opción no quede pegada
                Spacer(Modifier.height(16.dp))
            }

            // 3. Controles fijos (Siempre visibles al fondo)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.NavigateBefore, "Anterior")
                }

                IconButton(
                    onClick = onToggleAnswer,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            if (showAnswer) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primaryContainer,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (showAnswer) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Ver respuesta",
                        tint = if (showAnswer) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.NavigateNext, "Siguiente")
                }
            }
        }


    }
}

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun QuizOptionItem(
    option: com.jaco.cc3d.domain.models.QuizOption,
    showResult: Boolean,
    fontSize: Float
) {
    var isHovered by remember { mutableStateOf(false) }

    // Animación de escala: se agranda si hay hover o si es la respuesta correcta revelada
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = when {
            showResult && option.isCorrect -> 1.08f
            isHovered -> 1.05f
            else -> 1f
        }
    )

    // Determinar colores según el estado
    val borderColor = when {
        showResult && option.isCorrect -> Color(0xFF4CAF50)
        isHovered -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val containerColor = when {
        showResult && option.isCorrect -> Color(0xFFE8F5E9)
        isHovered -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f) // Centrado y con margen
            .graphicsLayer(scaleX = scale, scaleY = scale) // Efecto de zoom
            .onPointerEvent(androidx.compose.ui.input.pointer.PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(androidx.compose.ui.input.pointer.PointerEventType.Exit) { isHovered = false },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isHovered || (showResult && option.isCorrect)) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHovered) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de estado
            if (showResult && option.isCorrect) {
                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                Spacer(Modifier.width(12.dp))
            } else {
                Box(
                    Modifier
                        .size(12.dp)
                        .background(if (isHovered) MaterialTheme.colorScheme.primary else Color.LightGray, CircleShape)
                )
                Spacer(Modifier.width(12.dp))
            }

            Text(
                text = option.text,
                fontSize = (fontSize * 0.9f).sp, // Opciones un poquito más pequeñas que la pregunta
                lineHeight = (fontSize * 1.2f).sp,
                fontWeight = if (showResult && option.isCorrect) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun UserItem(connection: com.jaco.cc3d.data.network.Connection) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Un pequeño círculo verde para indicar "Online"
            Box(
                Modifier.size(10.dp).background(Color(0xFF4CAF50), CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = connection.name, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            AnimatedVisibility(
                visible = connection.isTakingQuiz,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "En examen",
                    tint = Color(0xFFEAA43A),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}