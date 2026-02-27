package com.jaco.cc3d.presentation.composables.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import javax.swing.Icon

@Composable
fun WarningButton(
    onClick: () -> Unit,
    enabled:  Boolean = true,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    text: String? = null,
    isOutlined: Boolean = false
) {
    ThemedIconTextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        icon = icon,
        text = text,
        isOutlined = isOutlined,
        // Colores personalizados (naranja/amarillo para advertencia)
        containerColor = Color(0xFFFFC107), // Un amarillo/naranja de advertencia
        contentColor = Color.Black // El texto negro es mejor sobre amarillo
    )
}