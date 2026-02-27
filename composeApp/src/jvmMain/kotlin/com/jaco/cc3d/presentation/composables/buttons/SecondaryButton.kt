package com.jaco.cc3d.presentation.composables.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import javax.swing.Icon

@Composable
fun SecondaryButton(
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
        // Colores secundarios
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    )
}