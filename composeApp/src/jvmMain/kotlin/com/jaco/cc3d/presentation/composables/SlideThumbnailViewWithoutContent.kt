package com.jaco.cc3d.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

// Asumiendo que SlideContent está definido

@Composable
fun SlideThumbnailViewWithoutContent(
    content: String,
    backgroundColor: Color,
    // Definimos un tamaño relativo para diferenciar la vista principal de las miniaturas
    isCurrent: Boolean
) {
    val contentSize = if (isCurrent) 18.sp else 16.sp
    val paddingValue = if (isCurrent) 16.dp else 8.dp
    val heightFraction = if (isCurrent) 0.9f else 0.5f

    // 🎨 Propiedades del Borde y Esquina
    val cornerRadius = 5.dp
    //val borderColor = if (isCurrent) Color.Gray else Color.Gray // Borde azul para la actual, gris para las demás
    val borderColor = if (isCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
    val borderWidth = if (isCurrent) 4.dp else 2.dp

    Column(
        modifier = Modifier
            //.width(if (isCurrent) 300.dp else 200.dp) // Tamaño dinámico
            //.height(if (isCurrent) 400.dp else 250.dp) // Altura dinámica
            .fillMaxWidth().fillMaxHeight(heightFraction)
            // 🔑 PASO 1: Recortar la forma para que el fondo sea redondeado
            .clip(RoundedCornerShape(cornerRadius))

            // 🔑 PASO 2: Aplicar el fondo (debe ir después del clip)
            .background(backgroundColor)

            // 🔑 PASO 3: Aplicar el borde redondeado (debe ir después del clip y background)
            .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(cornerRadius))

            .padding(paddingValue)
            .wrapContentSize(Alignment.Center)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {


        // Muestra una vista previa del contenido (solo las primeras palabras)
        Text(
            text = content,
            fontSize = contentSize,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            color = Color.Gray
        )
    }
}