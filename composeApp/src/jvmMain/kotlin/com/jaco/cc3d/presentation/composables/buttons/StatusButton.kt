package com.jaco.cc3d.presentation.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StatusButton(
    onClick: () -> Unit,
    containerColor: Color, // 🎨 Tú decides el color según el estado
    contentColor: Color,   // 🎨 El color del texto/icono
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    text: String? = null,
    isOutlined: Boolean = false,
    border: BorderStroke? = null
) {
    ThemedIconTextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        icon = icon,
        text = text,
        isOutlined = isOutlined,
        border = border,
        containerColor = if (isOutlined) Color.Transparent else containerColor,
        contentColor = if (isOutlined) containerColor else contentColor
    )
}