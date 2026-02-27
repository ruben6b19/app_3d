package com.jaco.cc3d.presentation.privado.studentDashboard.studentDisplay.util

import java.util.Locale

// 1. Estructura de datos para la pantalla de Estudiante
data class StudentDisplayContent(
    val controls: StudentControlStrings,
    val connection: ConnectionStrings,
    val bible: StudentBibleStrings,
    val quizzes: QuizStrings
)

data class StudentControlStrings(
    val backButton: String,
    val toggleBible: String,
    val prevSlide: String,
    val nextSlide: String,
    val slideCounter: (Int, Int) -> String,
    val showControls: String,
    val hideControls: String
)

data class QuizStrings(
    val title: String,
    val emptyList: String,
    val close: String,
    val next: String,
    val previous: String,
    val finish: String,
    val questionCounter: (Int, Int) -> String
)

data class ConnectionStrings(
    val connect: String,
    val disconnect: String,
    val setIp: String,
    val ipAddressLabel: String,
    val dialogTitle: String,
    val dialogDesc: String,
    val statusDisconnected: String,
    val statusSearching: (String, Int) -> String,
    val statusConnected: (String) -> String,
    val statusError: String,
    val statusNotFound: String,
    val retryIn: (Int) -> String,
    val retryAttempt: (Int, Int) -> String
)

data class StudentBibleStrings(
    val selectVersion: String,
    val loading: String,
    val noVerse: String
)

// 2. Recursos en Español e Inglés
object StudentDisplayResources {

    private val ES = StudentDisplayContent(
        controls = StudentControlStrings(
            backButton = "Salir",
            toggleBible = "Biblia",
            prevSlide = "Anterior",
            nextSlide = "Siguiente",
            slideCounter = { index, total -> "Diapositiva: $index / $total" },
            showControls = "Mostrar Controles",
            hideControls = "Ocultar Controles"
        ),
        connection = ConnectionStrings(
            connect = "Conectar",
            disconnect = "Desconectar",
            setIp = "Configurar IP",
            ipAddressLabel = "Dirección IP",
            dialogTitle = "Conectar al Maestro",
            dialogDesc = "Introduce la dirección IP del presentador (Ej: 192.168.1.50)",
            statusDisconnected = "Desconectado",
            statusSearching = { ip, port -> "Buscando en $ip:$port..." },
            statusConnected = { id -> "Sincronizado: $id" },
            statusError = "Error: El curso no existe",
            statusNotFound = "Maestro no encontrado",
            retryIn = { seconds -> "Reintentando en ${seconds}s..." },
            retryAttempt = { current, max -> "Intento: $current / $max" },
        ),
        bible = StudentBibleStrings(
            selectVersion = "Versión",
            loading = "Cargando versículo...",
            noVerse = "Referencia no encontrada."
        ),
        quizzes = QuizStrings(
            title = "Exámenes Disponibles",
            emptyList = "No hay exámenes activos para esta materia.",
            close = "Cerrar",
            next = "Siguiente",
            previous = "Anterior",
            finish = "Finalizar Examen",
            questionCounter = { current, total -> "Pregunta $current de $total" }
        )
    )

    private val EN = StudentDisplayContent(
        controls = StudentControlStrings(
            backButton = "Exit",
            toggleBible = "Bible",
            prevSlide = "Previous",
            nextSlide = "Next",
            slideCounter = { index, total -> "Slide: $index / $total" },
            showControls = "Show Controls",
            hideControls = "Hide Controls"
        ),
        connection = ConnectionStrings(
            connect = "Connect",
            disconnect = "Disconnect",
            setIp = "Set IP",
            ipAddressLabel = "IP Address",
            dialogTitle = "Connect to Teacher",
            dialogDesc = "Enter the presenter's IP address (e.g., 192.168.1.50)",
            statusDisconnected = "Disconnected",
            statusSearching = { ip, port -> "Searching on $ip:$port..." },
            statusConnected = { id -> "Synced: $id" },
            statusError = "Error: Course not found",
            statusNotFound = "Teacher not found",
            retryIn = { seconds -> "Retrying in ${seconds}s..." },
            retryAttempt = { current, max -> "Attempt: $current / $max" },

        ),
        bible = StudentBibleStrings(
            selectVersion = "Version",
            loading = "Loading verse...",
            noVerse = "Reference not found."
        ),
        quizzes = QuizStrings(
            title = "Available Quizzes",
            emptyList = "There are no active quizzes for this subject.",
            close = "Close",
            next = "Next",
            previous = "Previous",
            finish = "Finish Quiz",
            questionCounter = { current, total -> "Question $current of $total" }
        )
    )

    fun get(langCode: String): StudentDisplayContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}