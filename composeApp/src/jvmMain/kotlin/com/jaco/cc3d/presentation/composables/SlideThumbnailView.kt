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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.TextWithHoverableVerses
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.highlightVerses

// Asumiendo que SlideContent está definido

@Composable
fun SlideThumbnailView(
    title: String,
    content: String,
    backgroundColor: Color,
    // Definimos un tamaño relativo para diferenciar la vista principal de las miniaturas
    imageUrl: String? = null,
    svgRawCode: String? = null,
    isCurrent: Boolean,
    activeCitation: String? = null,
    onVerseHover: (String?) -> Unit,
    fontSize: TextUnit,
    fullScreen: Boolean = false,
    isNotesMode: Boolean = false,
) {
    val titleSize = if(fullScreen) 48.sp else if (isCurrent) 24.sp else 16.sp
    val contentSize = if (isCurrent) 16.sp else 12.sp

    val hasVisualContent = imageUrl != null || svgRawCode != null

    // 2. Calcular padding base según el contenido
    // Si hay imagen, queremos que ocupe casi todo el espacio (poco padding)
    // Si es texto, queremos márgenes amplios para lectura (mucho padding)
    val basePadding = if (hasVisualContent) {
        if (isCurrent) 16.dp else 4.dp  // Imagen: 16dp si es actual, casi nada si es miniatura
    } else {
        if (isCurrent) 64.dp else 16.dp // Texto: 64dp para aire superior/lateral, 16dp en miniatura
    }

    val cornerRadius = 5.dp
    val borderColor = if (isCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
    val borderWidth = if (isCurrent) 4.dp else 2.dp
    val heightFraction = when {
        fullScreen -> 1f
        isNotesMode -> 0.9f  // Si es modo notas, ocupamos casi todo el alto disponible
        isCurrent -> 0.9f
        else -> 0.5f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (fullScreen) 1f else heightFraction)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surface)
            .then(
                if (fullScreen) {
                    Modifier.padding(top = basePadding, start = basePadding)//.padding(horizontal = paddingValue)
                } else {
                    Modifier
                        .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(cornerRadius))
                        .padding(top = basePadding, start = basePadding)
                }
            ),
        //contentAlignment = Alignment.Center
            contentAlignment = Alignment.TopStart
    ) {
        when {
            svgRawCode != null -> {
                // 🎨 Renderizar SVG desde código directo
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(svgRawCode.toByteArray()) // Convertimos el XML a bytes
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            imageUrl != null -> {
                // 🌐 Renderizar desde URL
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(imageUrl)
                        .apply {
                            if(imageUrl.contains(".svg")) decoderFactory(SvgDecoder.Factory())
                        }
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                // --- VISTA DE TEXTO EN MINIATURA ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    //horizontalAlignment = Alignment.CenterHorizontally,
                    //verticalArrangement = Arrangement.Center
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    val processedText = highlightVerses(content)
                    TextWithHoverableVerses(
                        processedText,
                        fontSize = fontSize,
                        color = MaterialTheme.colorScheme.onSurface,
                        activeCitation = activeCitation,
                        onVerseHover = onVerseHover
                    )
                }
            }
        }
    }
}