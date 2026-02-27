package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel // 👈 Importación clave para incrustar Swing
import androidx.compose.ui.unit.dp
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.bibleCloseJob
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager.handleVerseHover
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JEditorPane
import javax.swing.JScrollPane
import javax.swing.SwingUtilities


import javax.swing.border.EmptyBorder
import javax.swing.text.DefaultCaret
import androidx.compose.ui.graphics.Color as ComposeColor


@Composable
fun RtfViewer(
    rtfContent: String,
    modifier: Modifier = Modifier,
    scrollValue: Int = 0,               // 👈 NUEVO
    onScrollChanged: (Int) -> Unit = {}
    // ⭐ AÑADIR NUEVO PARÁMETRO PARA CONTROLAR EL TAMAÑO DE FUENTE
   // fontSizeIncrement: Float = 0f // 0f significa tamaño normal (sin zoom)
) {
    val internalPaddingPx = 10
    val BEIGE = Color(245, 245, 220)
    val BEIGE_CLASICO = Color(245, 245, 220)

// Más claro y más amarillo (Crema)
    val BEIGE_CREMA = Color(255, 253, 208)

// Más oscuro y grisáceo (Hueso/Bone)
    val BEIGE_HUESO = Color(227, 218, 201)

// Beige más saturado, con un toque amarillo/naranja (Trigo/Wheat)
    val BEIGE_TRIGO = Color(245, 222, 179)

// Tono melocotón suave, cálido y vibrante (Mocasin)
    val BEIGE_MOCASIN = Color(255, 228, 181)

// Tono Arena, notablemente más oscuro para mejor contraste (Arena/Tan)
    val BEIGE_ARENA = Color(210, 180, 140)
    val composeBackgroundColor = ComposeColor(BEIGE_HUESO.rgb)

    SwingPanel(
        background = composeBackgroundColor,
        modifier = modifier.padding(1.dp),
        factory = {
            // 1. Crear el JEditorPane y JScrollPane
            val editorPane = JEditorPane().apply {
                contentType = "text/rtf"
                background = BEIGE_HUESO
                isEditable = false
                isOpaque = true
                //setForeground(Color.WHITE)
                // ⭐ APLICAR EL PADDING INTERNO CON EmptyBorder
                border = EmptyBorder(
                    internalPaddingPx,
                    internalPaddingPx,
                    internalPaddingPx,
                    internalPaddingPx
                )
                putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
                // Cargar el contenido inicial y establecer el zoom inicial
                text = rtfContent
                addMouseListener(object : MouseAdapter() {
                    override fun mouseEntered(e: MouseEvent?) {
                        // Accedemos al Job global para cancelarlo
                        bibleCloseJob?.cancel()
                    }

                    override fun mouseExited(e: MouseEvent?) {
                        // Opcional: Si quieres que empiece el conteo al salir del texto
                        handleVerseHover(null)
                    }
                })
                val caret = caret as DefaultCaret
                caret.updatePolicy = DefaultCaret.NEVER_UPDATE
            }

            // 2. Envolver en JScrollPane
            val scrollPane = JScrollPane(editorPane).apply {
                verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                //verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER
                //horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                background = BEIGE_HUESO
                border = null

                viewport.background = BEIGE_HUESO
                viewport.isOpaque = true
            }
            scrollPane.verticalScrollBar.addAdjustmentListener { e ->
                if (!e.valueIsAdjusting) { // Solo cuando termine el movimiento
                    onScrollChanged(e.value)
                }
            }
            scrollPane
        },
        update = { scrollPane ->
            val editor = (scrollPane as JScrollPane).viewport.view as JEditorPane
            val verticalBar = scrollPane.verticalScrollBar

            // 2. ACTUALIZACIÓN DE TEXTO
            if (editor.text != rtfContent) {
                editor.text = rtfContent

                // Forzar que el cursor vuelva al inicio después de cargar el texto
                editor.caretPosition = 0

                // Revalidar para que Swing calcule el nuevo tamaño del contenido
                editor.revalidate()

                // Si el texto cambió, queremos que empiece desde arriba a menos
                // que el scrollValue diga lo contrario inmediatamente.
                SwingUtilities.invokeLater {
                    verticalBar.value = scrollValue
                }
            }

            // 3. SINCRONIZACIÓN DE POSICIÓN
            // Usamos invokeLater para asegurar que el cambio de scroll ocurra
            // después de que Swing haya procesado el layout del texto.
            if (verticalBar.value != scrollValue) {
                SwingUtilities.invokeLater {
                    verticalBar.value = scrollValue
                }
            }
        }
        /*update = { scrollPane ->
            val editor = (scrollPane as JScrollPane).viewport.view as JEditorPane

            // 1. Actualizar Contenido
            if (editor.text != rtfContent) {
                editor.text = rtfContent
                // Al cambiar contenido, Swing a veces tarda en recalcular el tamaño
                // para el scroll, forzamos un revalidate.
                editor.revalidate()
            }

            // 2. APLICAR SCROLL (Para el Esclavo/Proyector)
            if (scrollPane.verticalScrollBar.value != scrollValue) {
                scrollPane.verticalScrollBar.value = scrollValue
            }
        }
        update = { scrollPane ->
            val editorPane = (scrollPane as JScrollPane).viewport.view as JEditorPane
            // 1. (CAMBIO) Actualizar el contenido RTF si ha cambiado (HACER ESTO PRIMERO)
            if (editorPane.text != rtfContent) {
                editorPane.text = rtfContent
                editorPane.caretPosition = 0
            }
            editorPane.revalidate()
        }*/

    )
}

