package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Broadcast // Usamos el ícono de Broadcast
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.focus.FocusRequester
import com.jaco.cc3d.presentation.composables.buttons.PrimaryButton
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.TeacherDisplayViewModel
import kotlinx.coroutines.launch

// Dentro de tu Composable DisplayScreen o el Composable que contiene el botón:

// Asumimos que tienes acceso a las funciones startWebsocketServer y stopWebsocketServer


@Composable
fun WebSocketControls(
    viewModel: TeacherDisplayViewModel,
    focusRequester: FocusRequester,
    startText: String,
    stopText: String,
    ) {
    // ⭐️ Estado para rastrear si el servidor está activo
    //var isServerRunning by remember { mutableStateOf(false) }

    val isServerRunning by viewModel.isServerRunning.collectAsState()
    val buttonEnabled by viewModel.buttonEnabled.collectAsState()
    //var viewMode by remember { mutableStateOf(ViewMode.CONTROLS) }
    val scope = rememberCoroutineScope()
    var isBusy by remember { mutableStateOf(false) }

    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    val emeraldGreen = Color(0xFF00A36C)
    val syncActiveGreen = Color(0xFF00A36C)
    val buttonDefaultColor = Color(0xFFF0F0F0)
    // Rojo para estado INACTIVO/LISTO (Sincronizar)
    val syncInactiveRed = Color(0xFFE30A14)
    // 1. ANIMACIÓN DE PULSACIÓN
    val infiniteTransition = rememberInfiniteTransition(label = "serverPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
        ),
        label = "pulseAlpha"
    )

    val buttonBorder: BorderStroke? = if (isServerRunning) {
        // 🔑 Borde Verde Animado cuando el servidor corre
        BorderStroke(
            width = 2.dp,
            color = syncActiveGreen.copy(alpha = pulseAlpha) // Color y Alpha animado
        )
    } else {
        // Borde fino de color secundario cuando está detenido
        BorderStroke(
            width = 1.dp,
            color = syncInactiveRed.copy(alpha = 0.5f)
        )
    }


    val iconColor = if (isServerRunning) syncActiveGreen else syncInactiveRed
    val buttonText = if (isServerRunning) stopText else startText

    PrimaryButton(
        onClick = {
            if (buttonEnabled) {
                scope.launch {
                    if (isServerRunning) viewModel.stopWebsocketServer()
                    else viewModel.startWebsocketServer()
                }
            }
            focusRequester.requestFocus()
        },
        enabled = buttonEnabled,
        isOutlined = true,
        border = buttonBorder, // 🎯 Aquí pasas el borde con la animación pulseAlpha
        icon = {
            Icon(
                imageVector = if (isServerRunning) Icons.Default.Stop else Icons.Default.Wifi,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        },
        text = buttonText,
    )

}
