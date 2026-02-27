package com.jaco.cc3d.presentation.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp



@Composable
fun ThemedIconTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: String?,
    containerColor: Color,
    contentColor: Color,
    isOutlined: Boolean,
    icon: (@Composable () -> Unit)? = null, // 🎯 Ahora es opcional y nulo por defecto
    border: BorderStroke? = null
) {
    // 1. Contenido común con lógica de nulidad
    val commonContent: @Composable RowScope.() -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 🎯 Solo renderizamos el icono si no es nulo
            icon?.let {
                it()
                if (text != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            text?.let {
                Text(text = it)
            }
        }
    }

    // 2. Lógica de renderizado del botón
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            border = border ?: BorderStroke(
                width = 2.dp,
                color = contentColor.copy(alpha = if (enabled) 1f else 0.38f),
            ),
            shape = RoundedCornerShape(percent = 8),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            content = commonContent
        )
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier,
            border = border,
            shape = RoundedCornerShape(percent = 8),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            content = commonContent
        )
    }
}
