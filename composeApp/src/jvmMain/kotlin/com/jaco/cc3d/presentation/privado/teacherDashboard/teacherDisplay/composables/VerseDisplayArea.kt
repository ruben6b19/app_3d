package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape // 👈 Importación clave para esquinas redondeadas
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // 👈 Importación clave para recortar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.bibleCloseJob
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.bibleScrollValueState
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.BibleDisplayStrings
import kotlin.math.roundToInt


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VerseDisplayArea(
    bibleId: String,
    citation: String?,
    onFetchVerseContent: suspend (bibleId: String, citation: String) -> String,
    modifier: Modifier = Modifier,
    currentBibleFontSize: Float = 0f,
    isMaster: Boolean = false,
    texts: BibleDisplayStrings
) {
    // Aquí verseContent contendrá la cadena RTF, no el texto simple
    var verseContent by remember { mutableStateOf<String>("") }
    var currentVisibleCitation by remember { mutableStateOf<String?>(null) }

    var lastRequestedKey by remember { mutableStateOf<Pair<String, String>?>(null) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val scrollState = rememberScrollState()
    val globalScrollValue by remember { bibleScrollValueState }

    // Lógica de Sincronización
    if (isMaster) {
        // El Master envía la posición a la variable global
        LaunchedEffect(scrollState.value) {
            bibleScrollValueState.value = scrollState.value
        }
    } else {
        // El Esclavo (proyector) recibe la posición y se mueve
        LaunchedEffect(globalScrollValue) {
            if (scrollState.value != globalScrollValue) {
                scrollState.scrollTo(globalScrollValue)
            }
        }
    }

    LaunchedEffect(bibleId, citation) {
        val citationToUse = if (citation.isNullOrEmpty()) currentVisibleCitation else citation

        if (!citationToUse.isNullOrEmpty()) {
            scrollState.scrollTo(0)
            if (isMaster) {
                bibleScrollValueState.value = 0
            }
        }
    }

    LaunchedEffect(bibleId, citation) {
        // 1. Determinar la cita
        val citationToUse = if (citation.isNullOrEmpty()) currentVisibleCitation else citation

        if (citationToUse.isNullOrEmpty()) return@LaunchedEffect

        // 2. Crear la clave de búsqueda
        val newKey = Pair(bibleId, citationToUse)

        // 3. Ejecutar solo si la combinación es distinta a la última procesada
        if (newKey != lastRequestedKey) {
            try {
                // Nota: Aquí puedes agregar un estado de "isLoading = true" si quieres
                val content = onFetchVerseContent(bibleId, citationToUse)

                // Actualizamos estados de éxito
                verseContent = content
                currentVisibleCitation = citationToUse
                lastRequestedKey = newKey
            } catch (e: Exception) {
                // Manejar error de carga aquí
                println("Error cargando versículo: ${e.message}")
            }
        }
    }

    Box(
        modifier = modifier
            // ⭐ 2. APLICAR EL OFFSET
            .onPointerEvent(PointerEventType.Enter) {
                bibleCloseJob?.cancel()
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }

            // ⭐ 3. DETECTAR GESTOS DE ARRASTRE
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume() // Consume el evento de arrastre
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .clip(RoundedCornerShape(4.dp)) // 👈 AÑADIDO: Recorta con esquinas redondeadas
            .background(Color.LightGray.copy(alpha = 0.9f)) // Fondo oscuro
            .border(3.dp, Color.Black, RoundedCornerShape(4.dp)) // 👈 AÑADIDO: Borde con esquinas redondeadas
            .padding(3.dp) // 👈 Padding interno
            .fillMaxHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.TopStart
        // ... tu diseño con bordes redondeados y padding
    ) {
        Column(
            // La columna es solo para apilar la cita y el visor
            //modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState())
            //modifier = Modifier.verticalScroll(scrollState)
            //modifier = Modifier.fillMaxHeight().verticalScroll(scrollState)
            modifier = Modifier.fillMaxSize()
        ) {
            // Mostrar la Cita (esto puede seguir siendo un Text normal)
            currentVisibleCitation?.let { textToShow ->
                Text(
                    text = textToShow, // ⭐️ Usamos el estado persistente
                    color = Color.Black,
                    // Define un tamaño para la cita (ej: un tamaño fijo o dependiente de currentFontSize)
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }

            // ⭐️ Usar el Visor RTF aquí en lugar de Text
            if (verseContent.isNotEmpty()) {
                RtfViewer(
                    rtfContent = revolverFragmentRTF(verseContent, currentBibleFontSize),
                   // modifier = Modifier.fillMaxWidth(),
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 40.dp), // 👈 IMPORTANTE: weight(1f)
                    scrollValue = globalScrollValue,
                    onScrollChanged = { newValue ->
                        if (isMaster) bibleScrollValueState.value = newValue
                    }
                    //modifier = Modifier.fillMaxSize().weight(1f),
                    //fontSizeIncrement = currentFontSize
                )
            } else {
                //Text("Cargando...")
                Text(
                    text = texts.emptySelection,
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineLarge,
                    //fontSize = currentFontSize,
                    modifier = Modifier.padding(10.dp),
                    fontWeight = FontWeight.Normal
                )
            }
        }
        // ⭐ PIE DE PÁGINA BONITO (BibleId)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 2.dp, end = 2.dp)
                .zIndex(1f) // 👈 CLAVE: Trae el componente a la capa superior
                .background(
                    color = Color(0xFF2C3E50),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = bibleId.uppercase(), // "NTV", "RVR1960", etc.
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

    }
}


// ... (función VerseDisplayArea)
//\red139\green0\blue0;     % ⭐ Índice 7 (\cf7): Borgoña Oscuro
//\red34\green139\blue34;   % ⭐ Índice 7 (\cf7): Verde Bosque Oscuro
fun revolverFragmentRTF(
    rtfFragment: String,
    fontSizeIncrement: Float = 0f // 👈 AÑADIDO: Recibe el Float
): String {
    val rtfHeader = "{\\rtf1\\ansi\\deff0"
    //val fontTable = "{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}}"
    //val fontTable = "{\\fonttbl{\\f0\\fswiss\\fcharset2 Arial;}}"
    //val fontTable = "{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}{\\f1\\fswiss\\fcharset2 Arial;}}"
    //val fontTable = "{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}{\\f1\\fswiss\\fcharset2 Arial\\cpg1253;}}"
    //val fontTable = "{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}{\\f1\\fswiss\\fcharset161\\cpg1253 Arial;}}"
    val fontTable = "{\\fonttbl{\\f0\\fnil\\fcharset0 Arial;}{\\f1\\fnil\\fcharset161 Arial;}}"
  //\fonttbl
    //    {\f0\fnil\fcharset0 Arial;}
    //    {\f1\fnil\fcharset161 SBL Greek;}

    val colorTable = """
    {\colortbl;
    \red0\green0\blue0;       % Índice 1 (\cf1): Negro (puede ser usado como default)
    \red255\green255\blue255; % Índice 2 (\cf2): Blanco fondo del parrafo
    \red0\green0\blue0;       % ⭐ Índice 3 (\cf3): Negro (¡SOLICITADO!)
    \red255\green255\blue0;   % Índice 4 (\cf4): Amarillo
    \red0\green128\blue0;     % Índice 5 (\cf5): Verde
    \red255\green0\blue0;     % Índice 6 (\cf6): Rojo (Palabras de Jesús)
    \red34\green139\blue34;   % Índice 7 (\cf7): 
    \red0\green0\blue0;       % ⭐ Índice 8 (\cf8): Negro (AÑADIDO PARA EVITAR ERROR)
    \red0\green0\blue0;       % ⭐ Índice 9 (\cf9): Negro (AÑADIDO PARA EVITAR ERROR)
    \red0\green0\blue0;       % ⭐ Índice 10 (\cf10): Negro (AÑADIDO PARA EVITAR ERROR)
    \red0\green128\blue0;       % ⭐ Índice 11 (\cf11): Negro (AÑADIDO PARA EVITAR EL ERROR Index 11)
    \red0\green0\blue0;
    \red0\green0\blue0;
    \red0\green0\blue0;
    \red128\green128\blue128;
    }
    """.trimIndent()

    // --- 👇 LÓGICA DEL TAMAÑO DE FUENTE ---
    // El tamaño en RTF se mide en "half-points". \fs24 = 12pt.

    // 1. Definimos un tamaño base (Ej: 48 = 24pt, un buen tamaño para proyectar)
    val baseFontSize = 48

    // 2. Definimos cuánto cambia por cada "paso" (0.1f)
    // El rango es -0.5 a +0.5. Queremos que cada paso (0.1f) sume/reste, digamos, 4 half-points (2pt).
    val incrementStep = 8 // 8 half-points = 4pt

    // 3. Calculamos los pasos. (ej: 0.3f / 0.1f = 3 pasos)
    val steps = (fontSizeIncrement / 0.1f).toInt()

    // 4. Calculamos el tamaño final (ej: 48 + (3 * 8) = 72)
    // Usamos coerceAtLeast para evitar tamaños negativos o muy pequeños.
    val finalFontSize = (baseFontSize + (steps * incrementStep)).coerceAtLeast(16) // Mínimo 8pt

    // 5. Usamos el tamaño final en el comando RTF
    val paragraphResetAndColor = "\\pard\\plain\\cf3\\f0\\fs$finalFontSize "
    // cf3 = Color de Texto (Negro), cb2 = Color de Fondo (Blanco)
    //val paragraphResetAndColor = "\\pard\\plain\\cf3\\cb2\\f0\\fs$finalFontSize "
    // --- FIN LÓGICA TAMAÑO ---


    val rtfFooter = "}"

    val cleanedFragment = rtfFragment.replace("\n", "\\par ")

    return "$rtfHeader$fontTable$colorTable$paragraphResetAndColor$cleanedFragment$rtfFooter"
}

