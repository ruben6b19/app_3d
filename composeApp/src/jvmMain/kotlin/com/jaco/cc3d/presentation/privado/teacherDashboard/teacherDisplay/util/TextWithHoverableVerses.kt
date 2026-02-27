package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import com.jaco.cc3d.common.AppConfig
import org.commonmark.renderer.text.TextContentRenderer
import org.commonmark.node.*
import org.commonmark.parser.Parser

import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableBody
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.ext.gfm.tables.TablesExtension

const val HIGHLIGHT_MARKER = "_"
private const val VERSE_TAG_HOVER = "verse_tag_hover"

// Archivo: TextWithHoverableVerses.kt

// ... (imports anteriores)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextWithHoverableVerses(
    text: String,
    fontSize: TextUnit,
    color: Color,
    activeCitation: String? = null,
    onVerseHover: (String?) -> Unit
) {

    // 1. Estado para guardar el rango completo de la anotación
    var hoveredVerseRange by remember { mutableStateOf<AnnotatedString.Range<String>?>(null) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val annotatedText = remember(text, activeCitation, fontSize, color) {
        buildAnnotatedString {
            val baseStyle = SpanStyle(fontSize = fontSize, color = color)
            val verseStyle = SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFFBC21FF))

            // Ahora, cada vez que fontSize cambie, esta función se vuelve a ejecutar
            applyMarkdownStyles(
                text = text,
                baseFontSize = fontSize,
                baseStyle = baseStyle,
                verseStyle = verseStyle,
                activeCitation = activeCitation
            )
        }
    }
    /*val annotatedText = remember(text, activeCitation) {
        buildAnnotatedString {
            // Define los estilos
            val baseStyle = SpanStyle(fontSize = fontSize, color = color)
            val verseStyle = SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFFBC21FF))

            // Llama a la función de estilo UNA SOLA VEZ con el texto completo.
            applyMarkdownStyles(
                text = text,
                baseFontSize = fontSize,
                baseStyle = baseStyle,
                //appendBlockLineBreaks = true, // 'true' para el comportamiento normal de MD
                verseStyle = verseStyle,      // Pasamos el estilo del versículo
                activeCitation = activeCitation
            )
        }
    }*/


// Uso
// convertMarkdownToWord("documento.md", "salida.docx")
    Box {
        Text(
            text = annotatedText,
            fontSize = fontSize,
            color = color,
            style = TextStyle(lineHeight = 1.5.em),
            onTextLayout = { textLayoutResult = it },
            modifier = Modifier
                .onPointerEvent(PointerEventType.Move) { event ->
                    val position = event.changes.first().position
                    textLayoutResult?.let { layoutResult ->
                        val offset = layoutResult.getOffsetForPosition(position)

                        val annotation = annotatedText.getStringAnnotations(VERSE_TAG_HOVER, offset, offset).firstOrNull()

                        // 🔑 LÓGICA DE DETECCIÓN Y LLAMADA AL CALLBACK
                        if (annotation != null) {
                            if (hoveredVerseRange?.item != annotation.item) {
                                // Se ha movido a una nueva cita
                                hoveredVerseRange = annotation
                                // 🔑 Llama al callback con la cita (esto actualiza el estado global)
                                onVerseHover(annotation.item)
                            }
                        } else {
                            // El mouse no está sobre ninguna cita.
                            if (hoveredVerseRange != null) {
                                // Estaba sobre una cita y ahora no lo está.
                                hoveredVerseRange = null
                                // 🔑 Llama al callback con null para ocultar el VerseDisplayArea
                                onVerseHover(null)
                            }
                        }
                    }
                }
                .onPointerEvent(PointerEventType.Exit) {
                    hoveredVerseRange = null
                    // 🔑 Llama al callback con null para ocultar el VerseDisplayArea
                    onVerseHover(null)
                }
        )
        // ❌ ELIMINADO: Todo el código del Popup ❌
    }
}

//val VerseShortcutNames = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
//val VerseShortcutNames = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

fun AnnotatedString.Builder.applyMarkdownStyles(
    text: String,
    baseFontSize: TextUnit,
    baseStyle: SpanStyle,
    verseStyle: SpanStyle,
    activeCitation: String? = null
) {
    val extensions = listOf(TablesExtension.create())
    val parser = Parser.builder().extensions(extensions).build()

    //val parser = Parser.builder().build()
    val document = parser.parse(text)
    val textContentRenderer = TextContentRenderer.builder().build()

    var citationIndex = 0

    fun buildStyledText(node: Node, currentStyle: SpanStyle) {
        when (node) {
            //ini tabla
            is TableBlock -> {
                // Aplicamos Monospace para que las columnas coincidan
                val tableStyle = currentStyle.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                var child = node.firstChild
                while (child != null) {
                    buildStyledText(child, tableStyle)
                    child = child.next
                }
                append("\n")
            }

            is TableHead -> {
                var child = node.firstChild
                while (child != null) {
                    buildStyledText(child, currentStyle.copy(fontWeight = FontWeight.Bold))
                    child = child.next
                }
            }

            is TableBody -> {
                var child = node.firstChild
                while (child != null) {
                    buildStyledText(child, currentStyle)
                    child = child.next
                }
            }

            is TableRow -> {
                var child = node.firstChild
                while (child != null) {
                    buildStyledText(child, currentStyle)
                    if (child.next != null) append(" | ") // Separador de columnas
                    child = child.next
                }
                append("\n")
                if (node.parent is TableHead) {
                    append("------------------------------------------\n")
                }
            }

            is TableCell -> {
                var child = node.firstChild
                while (child != null) {
                    // Si hay un párrafo dentro de la celda, extraemos su texto directamente
                    // para evitar el salto de línea que el bloque Paragraph añadiría.
                    if (child is Paragraph) {
                        var innerChild = child.firstChild
                        while (innerChild != null) {
                            buildStyledText(innerChild, currentStyle)
                            innerChild = innerChild.next
                        }
                    } else {
                        buildStyledText(child, currentStyle)
                    }
                    child = child.next
                }
            }
            //fin tabla
            is BlockQuote -> {
                // Estilo para el bloque de cita (gris e itálica)
                val quoteStyle = currentStyle.merge(SpanStyle(
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                ))

                // Dibujamos la barra y procesamos el contenido
                var child = node.firstChild
                while (child != null) {
                    append("▎ ") // Prefijo visual de la cita
                    buildStyledText(child, quoteStyle)
                    child = child.next
                }
                append("\n")
                // No hace falta append(\n) extra aquí porque el Paragraph interno lo pondrá
            }
            is Document -> {
                var child = node.firstChild
                while (child != null) {
                    buildStyledText(child, currentStyle)
                    child = child.next
                }
            }

            is Heading -> {
                val scaleFactor = when (node.level) {
                    1 -> 1.9f
                    2 -> 1.45f
                    3 -> 1.25f
                    4 -> 1.1f
                    else -> 1.0f // H5 y H6 se quedan en tamaño base o negrita
                }
                //val finalColor = if (node.level <= 2) {
                //    color
                //} else {
                //    color.copy(alpha = 0.7f) // Esto es una función de la clase Color, no es Composable.
                //}

                val headingStyle = SpanStyle(
                    //fontSize = baseFontSize * (if (node.level == 1) 1.5 else 1.2).toFloat(),
                    fontSize = baseFontSize * scaleFactor,
                    fontWeight = FontWeight.Bold,
                    //color = finalColor
                )
                withStyle(currentStyle.merge(headingStyle)) {
                    var child = node.firstChild
                    while (child != null) {
                        buildStyledText(child, currentStyle.merge(headingStyle))
                        child = child.next
                    }
                }
                append("\n\n")
            }

            is Paragraph -> {
                var child = node.firstChild
                while (child != null) {
                    buildStyledText(child, currentStyle)
                    child = child.next
                }
                append("\n")
            }

            is BulletList -> {
                var listItem = node.firstChild
                while (listItem != null) {
                    append("* ") // 👈 Aquí PRESERVAS el asterisco (o cambias por •)
                    var child = listItem.firstChild
                    while (child != null) {
                        buildStyledText(child, currentStyle)
                        child = child.next
                    }
                    listItem = listItem.next
                }
            }

            is OrderedList -> {
                var number = (node as OrderedList).startNumber
                var listItem = node.firstChild
                while (listItem != null) {
                    append("${number++}. ") // 👈 Aquí PRESERVAS el número
                    var child = listItem.firstChild
                    while (child != null) {
                        buildStyledText(child, currentStyle)
                        child = child.next
                    }
                    listItem = listItem.next
                }
            }

            is Emphasis -> {
                val rawContent = textContentRenderer.render(node).trim()

                // VALIDACIÓN DE LIBRO (Soporta "1 Juan", "1 Corintios", etc)
                val words = rawContent.split(" ")
                var isValidVerse = false
                for (i in 3 downTo 1) {
                    if (words.size >= i) {
                        val potentialBook = words.take(i).joinToString(" ")
                        if (normalizeBookName(potentialBook) != null) {
                            isValidVerse = true
                            break
                        }
                    }
                }

                if (isValidVerse) {
                    pushStringAnnotation(tag = VERSE_TAG_HOVER, annotation = rawContent)
                    withStyle(currentStyle.merge(verseStyle)) {
                        append(rawContent)
                    }
                    if (citationIndex < AppConfig.VerseShortcutNames.size) {
                        val isActive = rawContent == activeCitation

                        // Definimos el estilo según si está activa o no
                        val shortcutStyle = if (isActive) {
                            // ESTILO ACTIVO (Ej: Naranja brillante, negrita extra, un poco más grande)
                            SpanStyle(
                                color = Color(0xFFFF6D00), // Naranja vivo para destacar
                                fontSize = baseFontSize * 0.8f, // Un poco más grande que el inactivo
                                fontWeight = FontWeight.ExtraBold,
                                // Opcional: añadir un fondo sutil
                                // background = Color(0xFFFFE0B2)
                            )
                        } else {
                            // ESTILO INACTIVO (Gris discreto)
                            SpanStyle(
                                color = Color.Gray.copy(alpha = 0.5f),
                                fontSize = baseFontSize * 0.7f,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Aplicamos el estilo elegido
                        withStyle(shortcutStyle) {
                            append(" [${AppConfig.VerseShortcutNames[citationIndex]}]")
                        }
                        citationIndex++
                    }
                    pop()
                } else {
                    withStyle(currentStyle.merge(SpanStyle(fontStyle = FontStyle.Italic))) {
                        append(rawContent)
                    }
                }
            }

            is StrongEmphasis -> {
                withStyle(currentStyle.merge(SpanStyle(fontWeight = FontWeight.Bold))) {
                    var child = node.firstChild
                    while (child != null) {
                        buildStyledText(child, currentStyle.merge(SpanStyle(fontWeight = FontWeight.Bold)))
                        child = child.next
                    }
                }
            }

            is Text -> append((node as Text).literal)
            is SoftLineBreak -> append("\n")
            is HardLineBreak -> append("\n")
        }
    }

    buildStyledText(document, baseStyle)
}

