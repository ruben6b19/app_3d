package com.jaco.cc3d.presentation.composables.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color

@Composable
fun DangerButton(
    onClick: () -> Unit,
    enabled:  Boolean = true,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    text: String? = null,
    isOutlined: Boolean = false
) {
    val errorColor = MaterialTheme.colorScheme.error

    // El color de contraste sobre el color de error
    val onErrorColor = MaterialTheme.colorScheme.onError

    // Lógica para asignar los colores al componente base:
    val container = if (isOutlined) Color.Transparent else errorColor // Fondo transparente si es delineado

    // El color del contenido debe ser el color de error si es delineado (para verse sobre el fondo claro)
    val content = if (isOutlined) errorColor else onErrorColor

    ThemedIconTextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        icon = icon,
        text = text,
        isOutlined = isOutlined,
        // Colores de peligro
        containerColor = container,
        contentColor = content
    )
}