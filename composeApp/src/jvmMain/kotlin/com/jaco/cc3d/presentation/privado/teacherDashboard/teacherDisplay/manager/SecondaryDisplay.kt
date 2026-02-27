package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.manager

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.composables.SlideView
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.models.SlideContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import javax.swing.JFrame
import javax.swing.SwingUtilities
import androidx.compose.ui.input.key.*
import com.jaco.cc3d.common.AppConfig
import com.jaco.cc3d.data.local.preferences.SettingsManager
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.BibleDisplayStrings
import com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util.DisplayResources
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

// Estado compartido para la diapositiva actual
val currentSlideState = mutableStateOf<SlideContent?>(null)
// Lista para almacenar las referencias a las ventanas de las pantallas secundarias
var secondaryDisplayWindows = mutableListOf<JFrame>()
var currentBibleIdState = mutableStateOf<String>("NTV")

var bibleCloseJob: Job? = null
private val scope = CoroutineScope(Dispatchers.Main + Job())

val currentVerseCitationState = mutableStateOf<String?>(null)
val showBibleSidebarState = mutableStateOf<Boolean>(false)
val currentBibleFontSizeState = mutableStateOf<Float>(0F)
val mainContentFontSizeState = mutableStateOf(SettingsManager.load().fontSize)
val bibleScrollValueState = mutableStateOf(0)
val showSecondaryWindowsState = mutableStateOf(false)
val bibleTextsState = mutableStateOf<BibleDisplayStrings?>(null)
val controlsVisibleState = mutableStateOf(true)
val showCustomWindowState = mutableStateOf(false)
val connectedUsersState = mutableStateOf<List<com.jaco.cc3d.data.network.Connection>>(emptyList())

val connectedUsers by connectedUsersState

fun toggleCustomWindow(show: Boolean) {
    showCustomWindowState.value = show
}

fun updateFontSize(newSize: Float) {
    println("nuevo size: "+newSize)
    val clampedSize = newSize.coerceIn(10f, 100f) // Limitar tamaño
    mainContentFontSizeState.value = clampedSize
    // Aquí llamaremos al guardado persistente
    //saveSettings(clampedSize)
    SettingsManager.save(clampedSize)
}


fun handleVerseHover(citation: String?) {
    if (citation != null) {
        // Si entra a un versículo, cancelamos cualquier cierre pendiente
        bibleCloseJob?.cancel()
        currentVerseCitationState.value = citation
        showBibleSidebarState.value = true
    } else {
        // Si sale, esperamos 500ms antes de cerrar
        bibleCloseJob?.cancel()
        bibleCloseJob = scope.launch {
            delay(1000) // Tiempo suficiente para mover el mouse a la biblia
            showBibleSidebarState.value = false
            currentVerseCitationState.value = null
        }
    }
}

// Función para crear una ventana en una pantalla específica
fun createDisplayWindowOnScreen(screen: GraphicsDevice, onFetchVerseContent: suspend (String, String) -> String): JFrame {
    val screenBounds = screen.defaultConfiguration.bounds

    val frame = JFrame("Pantalla de Presentación", screen.defaultConfiguration)
    frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
    frame.setBounds(screenBounds.x, screenBounds.y, screenBounds.width, screenBounds.height)
    frame.isUndecorated = true



    val composePanel = ComposePanel()
    composePanel.isFocusable = true
    composePanel.setContent {
        val slideToShow by currentSlideState
        //val slideToShow by currentSlideState
        // 🔑 Obtener el estado de la cita
        //val citationToShow by currentVerseCitationState
        //val showBibleSidebar by showBibleSidebarState
        val currentBibleFontSize by currentBibleFontSizeState
        val currentBibleId by currentBibleIdState
        val bibleTexts = bibleTextsState.value

        val focusRequester = remember { FocusRequester() }

        // 2. 🎯 LANZAR EL PEDIDO DE FOCO CUANDO APAREZCA
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        MaterialTheme {
            // Usa una vista de diapositiva que pueda manejar nulos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // 🔑 ESTA ES LA CLAVE:
                    .focusRequester(focusRequester)
                    .focusable()
                    .onKeyEvent { keyEvent ->
                        // Aquí replicas la lógica de navegación (Next/Previous)
                        // que ya tienes en TeacherDisplayScreen.kt
                        handleGlobalKeyEvent(
                            keyEvent = keyEvent,
                            onCloseWindows = {
                                // Lógica para cerrar desde la secundaria si se presiona Escape
                                manageSecondaryDisplays(false, onFetchVerseContent)
                            }
                        )
                    }
            ) {
                SlideView(
                    slideToShow,
                    onFetchVerseContent,
                    //showBibleSidebar = showBibleSidebar,
                    currentBibleFontSize = currentBibleFontSize,
                    selectedBibleId = currentBibleId,
                    texts = bibleTexts ?: DisplayResources.get("es").bible,
                    users = connectedUsers
                    //currentCitation = citationToShow
                )
            }
        }
    }
    frame.addWindowListener(object : WindowAdapter() {
        override fun windowOpened(e: WindowEvent?) {
            // Cuando la ventana se abre, forzamos que el panel de Compose agarre el foco
            composePanel.requestFocusInWindow()
            composePanel.requestFocus()
        }
    })

    frame.contentPane.add(composePanel)
    return frame
}

// Lógica principal para gestionar todas las pantallas secundarias
fun manageSecondaryDisplays(show: Boolean, onFetchVerseContent: suspend (String, String) -> String) {
    SwingUtilities.invokeLater {
        if (show) {
            val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            val allScreens = ge.screenDevices

            // Limpia la lista antes de crear nuevas ventanas
            secondaryDisplayWindows.forEach { it.dispose() }
            secondaryDisplayWindows.clear()

            // Si hay más de una pantalla, itera y crea una ventana para cada una excepto la principal (índice 0)
            if (allScreens.size > 1) {
                // Itera desde el índice 1, ya que el índice 0 es la pantalla principal
                for (i in 1 until allScreens.size) {
                    val secondaryScreenDevice = allScreens[i]
                    val newWindow = createDisplayWindowOnScreen(secondaryScreenDevice, onFetchVerseContent)
                    secondaryDisplayWindows.add(newWindow)
                }

                // Muestra todas las ventanas creadas
                secondaryDisplayWindows.forEach { it.isVisible = true }
                println("Ventanas de presentación mostradas en todas las pantallas secundarias.")

            } else {
                println("No se detectaron pantallas secundarias.")
            }
        } else {
            // Oculta y cierra todas las ventanas
            secondaryDisplayWindows.forEach { it.isVisible = false; it.dispose() }
            secondaryDisplayWindows.clear()
            println("Ventanas de pantalla secundaria ocultas y eliminadas.")
        }
    }
}


var currentSlideIndexState = mutableStateOf(0)
var totalSlidesState = mutableStateOf(0)
var versesInCurrentSlideState = mutableStateOf<List<String>>(emptyList())
const val scrollSpeed = 50

fun handleGlobalKeyEvent(
    keyEvent: KeyEvent,
    onCloseWindows: () -> Unit // Para el Escape y F
): Boolean {
    if (keyEvent.type != KeyEventType.KeyDown) return false

    // 1. Lógica de Scroll de Biblia
    if (showBibleSidebarState.value) {
        when (keyEvent.key) {
            Key.DirectionUp -> {
                bibleScrollValueState.value = (bibleScrollValueState.value - scrollSpeed).coerceAtLeast(0)
                return true
            }
            Key.DirectionDown -> {
                bibleScrollValueState.value += scrollSpeed
                return true
            }
        }
    }

    // 2. Lógica de Teclas Rápidas para Versículos (Q, W, E...)
    val keyIndex = AppConfig.verseKeys.indexOf(keyEvent.key)
    val versesInSlide = versesInCurrentSlideState.value
    if (keyIndex != -1 && keyIndex < versesInSlide.size) {
        handleVerseHover(versesInSlide[keyIndex])
        return true
    }

    // 3. Navegación y Control
    return when (keyEvent.key) {
        Key.Z, Key.Equals -> { // Tecla +
            updateFontSize(mainContentFontSizeState.value + 2f)
            true
        }
        Key.X -> { // Tecla -
            updateFontSize(mainContentFontSizeState.value - 2f)
            true
        }
        Key.Q -> {
            showBibleSidebarState.value = false
            handleVerseHover(null)
            true
        }
        Key.W -> {
            // Invierte el estado actual (Toggle)
            controlsVisibleState.value = !controlsVisibleState.value
            true
        }
        Key.E -> {
            showCustomWindowState.value = !showCustomWindowState.value
            true
        }
        Key.DirectionLeft -> {
            if (currentSlideIndexState.value > 0) {
                currentSlideIndexState.value--
            }
            true
        }
        Key.DirectionRight -> {
            if (currentSlideIndexState.value < totalSlidesState.value - 1) {
                currentSlideIndexState.value++
            }
            true
        }
        Key.Escape -> {
            println("escape presionado")
            showSecondaryWindowsState.value = false
            onCloseWindows()
            true
        }
        else -> false
    }
}