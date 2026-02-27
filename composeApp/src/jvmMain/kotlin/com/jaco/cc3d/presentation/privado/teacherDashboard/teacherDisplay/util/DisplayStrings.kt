package com.jaco.cc3d.presentation.privado.teacherDashboard.teacherDisplay.util

import java.util.Locale

// 1. Estructura de datos para la pantalla de Display
data class DisplayContent(
    val controls: DisplayControlStrings,
    val bible: BibleDisplayStrings,
    val messages: DisplayMessageStrings,
    val errors: DisplayErrorStrings
)

data class DisplayControlStrings(
    val backButton: String,
    val bibleMenu: String,
    val fontSize: String,
    val syncStart: String,
    val syncStop: String,
    val loadMarkdown: String,
    val modeNotes: String,
    val modePresentation: String,
    val modeControls: String,
    val prevSlide: String,
    val nextSlide: String,
    val currentSlideLabel: (Int, Int) -> String, // Formato: "Diapositiva: 1/10"
    val emptySlide: String,
    val presenterNotes: String,
    val noNotes: String,
    val examTooltip: String
)

data class BibleDisplayStrings(
    val emptySelection: String,
    val loading: String,
    val versionLabel: String,
    val verseNotFound: String
)

data class DisplayMessageStrings(
    val waitingAction: String,
    val openingSelector: String,
    val fileLoaded: (Int) -> String,
    val loadFailed: String,
    val serverStarting: String,
    val serverReady: (String) -> String,
)

data class DisplayErrorStrings( // 🆕 Nueva data class
    val genericError: String,
    val serverPortError: String,
    val loadQuizError: String,
    val noLocalNetwork: String
)
// 2. Recursos en Español e Inglés
object DisplayResources {

    private val ES = DisplayContent(
        controls = DisplayControlStrings(
            backButton = "Volver",
            bibleMenu = "Seleccionar Biblia",
            fontSize = "Tamaño de fuente",
            syncStart = "Sincronizar",
            syncStop = "Detener",
            loadMarkdown = "Cargar(.md)",
            modeNotes = "Modo Notas",
            modePresentation = "Modo Presentación",
            modeControls = "Modo Controles",
            prevSlide = "Anterior",
            nextSlide = "Siguiente",
            currentSlideLabel = { index, total -> "Diapositiva: $index/$total" },
            emptySlide = "Vacío",
            presenterNotes = "Notas del Presentador",
            noNotes = "No hay notas disponibles.",
            examTooltip = "Abrir Examen"
        ),
        bible = BibleDisplayStrings(
            emptySelection = "Selecciona una Cita Bíblica",
            loading = "Cargando...",
            versionLabel = "Versión:",
            verseNotFound = "Referencia no encontrada."
        ),
        messages = DisplayMessageStrings(
            waitingAction = "Esperando acción...",
            openingSelector = "Abriendo selector...",
            fileLoaded = { size -> "¡Archivo cargado ($size caracteres)!" },
            loadFailed = "Carga cancelada o fallida.",
            serverStarting = "Iniciando servidor...",
            serverReady = { ip -> "Servidor levantado en: $ip" },

        ),
        errors = DisplayErrorStrings(
            genericError = "Error",
            serverPortError = "Error: No se pudo abrir ningún puerto (8081-8089)",
            loadQuizError = "Error al cargar exámenes",
            noLocalNetwork = "No detectamos una red local. Conéctate a una red Wi-Fi o Ethernet para sincronizar."
        )
    )

    private val EN = DisplayContent(
        controls = DisplayControlStrings(
            backButton = "Back",
            bibleMenu = "Select Bible",
            fontSize = "Font Size",
            syncStart = "Sync",
            syncStop = "Stop",
            loadMarkdown = "Load(.md)",
            modeNotes = "Notes Mode",
            modePresentation = "Presentation Mode",
            modeControls = "Controls Mode",
            prevSlide = "Previous",
            nextSlide = "Next",
            currentSlideLabel = { index, total -> "Slide: $index/$total" },
            emptySlide = "Empty",
            presenterNotes = "Presenter Notes",
            noNotes = "No notes available.",
            examTooltip = "Open Exam"
        ),
        bible = BibleDisplayStrings(
            emptySelection = "Select a Bible Citation",
            loading = "Loading...",
            versionLabel = "Version:",
            verseNotFound = "Reference not found."
        ),
        messages = DisplayMessageStrings(
            waitingAction = "Waiting for action...",
            openingSelector = "Opening selector...",
            fileLoaded = { size -> "File loaded ($size characters)!" },
            loadFailed = "Load cancelled or failed.",
            serverStarting = "Starting server...",
            serverReady = { ip -> "Server running at: $ip" }
        ),
        errors = DisplayErrorStrings(
            genericError = "Error",
            serverPortError = "Error: Could not open any port (8081-8089)",
            loadQuizError = "Error loading quizzes",
            noLocalNetwork = "No local network detected. Please connect to Wi-Fi or Ethernet to sync."
        )
    )

    fun get(langCode: String): DisplayContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}