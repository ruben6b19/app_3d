package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util

import androidx.compose.ui.graphics.Color
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.SlideContent
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.util.UUID.randomUUID

/**
 * Convierte texto Markdown en una lista de diapositivas incrementales.
 * - Un H1 (#) reinicia la acumulación (Nueva Diapositiva Maestra).
 * - Cualquier otro contenido (##, texto) se añade al contenido anterior (Paso incremental).
 */
suspend fun parseMarkdownToIncrementalSlides(markdownText: String): List<SlideContent> {
    val result = mutableListOf<SlideContent>()
    val blocks = markdownText.split(Regex("(\\n\\s*\\n)+")).filter { it.isNotBlank() }
    val regexId = Regex("""\{#([a-f0-9\-]{24,36})\}""")

    var masterSlideCounter = 0

    blocks.forEach { block ->
        masterSlideCounter++
        val idMatch = regexId.find(block)
        val baseId = idMatch?.groupValues?.get(1) ?: "temp-${masterSlideCounter}"
        val trimmedBlock = block.replace(regexId, "").trim()

        // 1. LÓGICA DE IMAGEN (Igual)
        val isImageUrl = trimmedBlock.startsWith("http") &&
                trimmedBlock.contains(Regex("\\.(jpg|jpeg|png|webp|gif)", RegexOption.IGNORE_CASE))

        // 2. LÓGICA DE TABLA (Nueva)
        // Buscamos el patrón de una tabla: una línea con | seguida de una línea de separación |---|
        val isTable = trimmedBlock.contains(Regex("""(?m)^\|.*\|.*\n\|[\s-]*:?---:?[\s-]*\|"""))

        if (isImageUrl) {
            result.add(SlideContent(
                id = baseId,
                title = "Imagen",
                contentText = "",
                masterSlideIndex = masterSlideCounter,
                imageUrl = trimmedBlock
            ))
        }
        else if (isTable) {
            // 🎯 SI ES TABLA: La mandamos completa en una sola diapositiva
            // No usamos acumulativo porque la tabla se rompería
            result.add(SlideContent(
                id = baseId,
                title = "Tabla Informativa",
                contentText = trimmedBlock,
                masterSlideIndex = masterSlideCounter
            ))
        }
        // 3. LÓGICA SVG (Igual)
        else if (trimmedBlock.startsWith("http") && trimmedBlock.contains(".svg", true)) {
            val rawSvg = fetchSvgContent(trimmedBlock)
            if (rawSvg != null) {
                val fragmentIds = Regex("""id="g(\d+)"""").findAll(rawSvg)
                    .map { it.groupValues[1].toInt() }.distinct().sorted().toList()

                if (fragmentIds.isEmpty()) {
                    result.add(SlideContent(id = baseId, title = "Diagrama", contentText = "", masterSlideIndex = masterSlideCounter, svgRawCode = trimmedBlock))
                } else {
                    fragmentIds.forEach { step ->
                        result.add(SlideContent(
                            id = "$baseId-step-$step",
                            title = "Paso $step",
                            contentText = "",
                            masterSlideIndex = masterSlideCounter,
                            svgRawCode = hideFutureSvgFragments(rawSvg, step)
                        ))
                    }
                }
            }
        }
        // 4. LÓGICA DE TEXTO ACUMULATIVO (Para párrafos y títulos normales)
        else {
            val lines = trimmedBlock.lines().filter { it.isNotBlank() }
            val currentAccumulator = StringBuilder()
            var currentTitle = ""

            lines.forEachIndexed { index, line ->
                if (index == 0) currentTitle = line.replace(Regex("^#+\\s*"), "")

                if (currentAccumulator.isNotEmpty()) currentAccumulator.append("\n\n")
                currentAccumulator.append(line)

                result.add(SlideContent(
                    id = "$baseId-line-$index",
                    title = currentTitle,
                    contentText = currentAccumulator.toString(),
                    masterSlideIndex = masterSlideCounter
                ))
            }
        }
    }
    return result
}

suspend fun parseMarkdownToIncrementalSlides2(markdownText: String): List<SlideContent> {
    val result = mutableListOf<SlideContent>()

    // 1. Separamos los bloques por saltos de línea dobles
    val blocks = markdownText.split(Regex("(\\n\\s*\\n)+")).filter { it.isNotBlank() }

    // 2. Definimos el patrón para encontrar el ID del administrador {#...}
    val regexId = Regex("""\{#([a-f0-9\-]{24,36})\}""")

    var masterSlideCounter = 0

    blocks.forEach { block ->
        masterSlideCounter++

        // 3. Extraer el ID puesto por el administrador
        val idMatch = regexId.find(block)
        val baseId = idMatch?.groupValues?.get(1) ?: "temp-${masterSlideCounter}"

        // 4. Limpiar el contenido: Quitamos el {#id} para que la UI esté limpia
        val trimmedBlock = block.replace(regexId, "").trim()

        // --- LÓGICA DE IMAGEN ---
        val isImageUrl = trimmedBlock.startsWith("http") &&
                trimmedBlock.contains(Regex("\\.(jpg|jpeg|png|webp|gif)", RegexOption.IGNORE_CASE))

        if (isImageUrl) {
            result.add(SlideContent(
                id = baseId,
                title = "Imagen",
                contentText = "",
                masterSlideIndex = masterSlideCounter,
                imageUrl = trimmedBlock
            ))
        }
        // --- LÓGICA SVG ---
        else if (trimmedBlock.startsWith("http") && trimmedBlock.contains(".svg", true)) {
            val rawSvg = fetchSvgContent(trimmedBlock)
            if (rawSvg != null) {
                val fragmentIds = Regex("""id="g(\d+)"""").findAll(rawSvg)
                    .map { it.groupValues[1].toInt() }.distinct().sorted().toList()

                if (fragmentIds.isEmpty()) {
                    result.add(SlideContent(id = baseId, title = "Diagrama", contentText = "", masterSlideIndex = masterSlideCounter, svgRawCode = trimmedBlock))
                } else {
                    fragmentIds.forEach { step ->
                        result.add(SlideContent(
                            id = "$baseId-step-$step",
                            title = "Paso $step",
                            contentText = "",
                            masterSlideIndex = masterSlideCounter,
                            svgRawCode = hideFutureSvgFragments(rawSvg, step)
                        ))
                    }
                }
            }
        }
        // --- LÓGICA DE TEXTO ACUMULATIVO ---
        else {
            val lines = trimmedBlock.lines().filter { it.isNotBlank() }
            val currentAccumulator = StringBuilder()
            var currentTitle = ""

            lines.forEachIndexed { index, line ->
                if (index == 0) currentTitle = line.replace(Regex("^#+\\s*"), "")

                if (currentAccumulator.isNotEmpty()) currentAccumulator.append("\n\n")
                currentAccumulator.append(line)

                result.add(SlideContent(
                    id = "$baseId-line-$index",
                    title = currentTitle,
                    contentText = currentAccumulator.toString(),
                    masterSlideIndex = masterSlideCounter
                ))
            }
        }
    }
    return result
}

fun hideFutureSvgFragments(svgCode: String, currentStep: Int): String {
    var processed = svgCode
    val maxPossibleFragments = 25

    for (i in (currentStep + 1)..maxPossibleFragments) {
        // Usamos una búsqueda más precisa para no romper el XML
        val idToHide = "id=\"g$i\""
        if (processed.contains(idToHide)) {
            // Reemplazamos el ID por el ID + el estilo para ocultar
            // Usamos display:none porque es más efectivo que visibility:hidden en SVGs
            processed = processed.replace(idToHide, "$idToHide style=\"display:none;\"")
        }
    }

    // 💡 TRUCO EXTRA: Asegurarnos de que el texto no tenga unidades 'px' que bloqueen el renderizado
    // Algunos editores de SVG exportan font-size="32px" y eso rompe Android
    processed = processed.replace("px\"", "\"")

    return processed
}

fun ensureMarkdownIds(markdownText: String): String {
    // 1. Separamos por títulos o URLs
    val separator = Regex("""(?m)^(?=#|https?://)""")
    val blocks = markdownText.split(Regex("(\\n\\s*\\n)+")).filter { it.isNotBlank() }
    //val blocks = markdownText.split(separator).filter { it.isNotBlank() }

    // 2. 🎯 CLAVE: Buscamos el formato {#...}
    // Este Regex busca un ID de 24 a 36 caracteres dentro de llaves
    val regexId = Regex("""\{#([a-f0-9\-]{24,36})\}""")

    val updatedBlocks = blocks.map { block ->
        val trimmedBlock = block.trim()

        // 3. Como tu MD original no tiene "{#...}", esto dará FALSE
        if (regexId.containsMatchIn(trimmedBlock)) {
            trimmedBlock
        } else {
            // 🛑 AHORA SÍ SE DETRÁ AQUÍ EL DEPURADOR
            val newId = randomUUID().toString()

            // 4. Inyectamos el ID al final de la primera línea (el título)
            // O al principio del bloque si prefieres
            "{#$newId}\n$trimmedBlock"
        }
    }
    return updatedBlocks.joinToString("\n\n")
}

private val client = io.ktor.client.HttpClient()
suspend fun fetchSvgContent(url: String): String? {
    return try {
        client.get(url).bodyAsText()
    } catch (e: Exception) {
        null
    }
}