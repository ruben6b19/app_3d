package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text // Asumiendo Material 3
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.bibleCloseJob
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.currentVerseCitationState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.handleVerseHover
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.showBibleSidebarState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.SlideContent
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.BibleDisplayStrings
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.TextWithHoverableVerses
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.highlightVerses
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest // 👈 Esta es la que te falta
import coil3.svg.SvgDecoder
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.mainContentFontSizeState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.showCustomWindowState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SlideView(
    slide: SlideContent?,
    onFetchVerseContent: suspend (bibleId: String, citation: String) -> String,
    currentBibleFontSize: Float,
    selectedBibleId: String,
    texts: BibleDisplayStrings,
    users: List<com.jaco.cc3d.data.network.Connection>?= null
) {
    if (slide == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay diapositiva seleccionada", fontSize = 24.sp)
        }
        return
    }


    val slideBackgroundColor = MaterialTheme.colorScheme.surface


    // 2. OBTENER EL COLOR DEL TEXTO DEL TEMA
    // Este color ya respeta el tema (onSurface en Light y Dark)
    val themeTextColor = MaterialTheme.colorScheme.onSurface

    // 3. CALCULAR EL COLOR FINAL DEL TEXTO
    val finalTextColor = if (slide.backgroundColor != Color.Unspecified) {
        // Si el usuario forzó un color de fondo manual, calculamos la luminosidad
        if (slide.backgroundColor.luminance() > 0.5f) Color.Black else Color.White
    } else {
        // Si usamos el color del tema, usamos el color onSurface del tema
        themeTextColor
    }



    val globalCitation by remember { currentVerseCitationState }
    //val mainContentFontSize by remember { mainContentFontSizeState }
    val mainContentFontSize =  mainContentFontSizeState.value
    val globalShowBible by remember { showBibleSidebarState }
    val showCustomWindow by showCustomWindowState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(slideBackgroundColor)
            .padding(0.dp),
        //contentAlignment = Alignment.Center
        contentAlignment = Alignment.TopStart
    ) {
        when {
            slide.svgRawCode != null -> {
                // 🎨 Renderizar SVG desde código directo
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(slide.svgRawCode.toByteArray()) // Convertimos el XML a bytes
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            slide.imageUrl != null -> {
                // 🌐 Renderizar desde URL
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(slide.imageUrl)
                        .apply {
                            if(slide.imageUrl.contains(".svg")) decoderFactory(SvgDecoder.Factory())
                        }
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize() // CAMBIO 2: Ocupar todo el espacio para controlar el padding superior
                        .padding(top = 120.dp, start = 80.dp, end = 80.dp), // Espaciado fijo
                    horizontalAlignment = Alignment.Start, // Alineado a la izquierda para estilo lista
                    verticalArrangement = Arrangement.Top
                    //modifier = Modifier.wrapContentSize().padding(28.dp),
                    //horizontalAlignment = Alignment.CenterHorizontally,
                    //verticalArrangement = Arrangement.Center
                ) {

                    val processedText = highlightVerses(slide.contentText)

                    /*val lines = slide.contentText.lines()
                    lines.forEachIndexed { index, line ->
                        TextWithHoverableVerses(
                            highlightVerses(line),
                            fontSize = if (index == 0) 45.sp else 37.sp, // Título un poco más grande
                            //fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            //onFetchVerseContent = onFetchVerseContent
                            activeCitation = globalCitation,
                            onVerseHover = { citation ->
                                handleVerseHover(citation)
                            }
                        )
                        //Spacer(modifier = Modifier.height(16.dp))
                    }*/
                    TextWithHoverableVerses(
                        processedText,
                        fontSize = mainContentFontSize.sp,
                        //color = if (slide.backgroundColor.luminance() > 0.5f) Color.Black else Color.White,
                        //color = finalTextColor,
                        color = MaterialTheme.colorScheme.onSurface,
                        //onFetchVerseContent = onFetchVerseContent
                        activeCitation = globalCitation,
                        onVerseHover = { citation ->
                            handleVerseHover(citation)
                        }
                    )

                }
                if (globalShowBible) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .fillMaxHeight()
                            .padding(bottom = 130.dp)
                            .align(Alignment.CenterEnd)
                            // 🎯 CLAVE: Si el mouse entra aquí, cancelamos el cierre (el panel se queda abierto)
                            .onPointerEvent(PointerEventType.Enter) {
                                bibleCloseJob?.cancel()
                            }
                            // Si el mouse sale de este Box lateral, ejecutamos el cierre con delay
                            .onPointerEvent(PointerEventType.Exit) {
                                handleVerseHover(null)
                            }
                    ) {
                        VerseDisplayArea(
                            bibleId = selectedBibleId,
                            citation = if(globalCitation.isNullOrEmpty()) null else globalCitation,
                            onFetchVerseContent = onFetchVerseContent,
                            // Llenamos el Box contenedor
                            modifier = Modifier.fillMaxSize(),
                            currentBibleFontSize = currentBibleFontSize,
                            isMaster = false,
                            texts = texts,
                        )
                    }
                }
                if (showCustomWindow) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.35f) // Que ocupe un porcentaje de la pantalla
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd)
                            .padding(bottom = 130.dp) // Margen inferior igual al de tu biblia
                    ) {
                        CustomEmptyOverlay(users = users ?: emptyList(),modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}


// ... (después de SlideView)





