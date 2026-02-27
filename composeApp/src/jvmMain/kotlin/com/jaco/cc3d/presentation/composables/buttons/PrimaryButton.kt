package com.jaco.cc3d.presentation.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import javax.swing.Icon

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    text: String? = null,
    isOutlined: Boolean = false,
    border: BorderStroke? = null
) {
    // Colores base para el botón primario
    val primaryColor = MaterialTheme.colorScheme.primary // Color principal para el relleno
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary // Color de contraste sobre el primario (contenido)
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer // Contenedor primario (opción para el delineado)

    // Lógica para asignar los colores:
    // El fondo es transparente si es delineado, sino usa el color primario (o primaryContainer para el 'Tonal Button', aquí usamos primary)
    val container = if (isOutlined) Color.Transparent else primaryColor

    // El color del contenido (texto/icono) debe ser el color primario si es delineado (para verse sobre el fondo claro),
    // sino usa el color de contraste sobre el primario.
    val content = if (isOutlined) primaryColor else onPrimaryColor

    ThemedIconTextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        icon = icon,
        text = text,
        isOutlined = isOutlined,
        // Colores primarios (de marca)
        border = border,
        containerColor = container,
        contentColor = content
    )
}