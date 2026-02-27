package com.jaco.cc3d.presentation.privado.studentDashboard.util

import java.util.Locale

// 1. Data Class Padre
data class StudentDashboardContent(
    val welcome: StudentWelcomeStrings,
    val list: StudentEnrollmentListStrings,
    val feedback: StudentDashboardFeedback
)

// 2. Sub-clases
data class StudentWelcomeStrings(
    val greeting: (String) -> String,
    val subtitle: String,
    val titleScreen: String
)

data class StudentEnrollmentListStrings(
    val enrolledOn: String,
    val groupLabel: String,
    val yearLabel: String,
    val emptyMessage: String,
    val retryButton: String,
    val unknownSubject: String
)

data class StudentDashboardFeedback(
    val loading: String,
    val loadError: String,
    val sessionExpired: String,
    val unknownError: String
)

// 3. Objeto Singleton con las traducciones
object StudentDashboardResources {

    private val ES = StudentDashboardContent(
        welcome = StudentWelcomeStrings(
            greeting = { name -> "¡Hola, $name!" },
            subtitle = "Estas son tus materias inscritas para este periodo.",
            titleScreen = "Mis Materias"
        ),
        list = StudentEnrollmentListStrings(
            enrolledOn = "Inscrito el:",
            groupLabel = "Gr:", // O dejarlo vacío si solo usas el color
            yearLabel = "Año:",
            emptyMessage = "No se encontraron materias inscritas.",
            retryButton = "Reintentar",
            unknownSubject = "Materia sin nombre"
        ),
        feedback = StudentDashboardFeedback(
            loading = "Cargando tus materias...",
            loadError = "Error al cargar tus materias",
            sessionExpired = "La sesión ha expirado.",
            unknownError = "Ha ocurrido un error inesperado."
        )
    )

    private val EN = StudentDashboardContent(
        welcome = StudentWelcomeStrings(
            greeting = { name -> "Hello, $name!" },
            subtitle = "These are your enrolled subjects for this period.",
            titleScreen = "My Subjects"
        ),
        list = StudentEnrollmentListStrings(
            enrolledOn = "Enrolled on:",
            groupLabel = "Gr:",
            yearLabel = "Year:",
            emptyMessage = "No enrolled subjects found.",
            retryButton = "Retry",
            unknownSubject = "Unnamed Subject"
        ),
        feedback = StudentDashboardFeedback(
            loading = "Loading your subjects...",
            loadError = "Error loading your subjects",
            sessionExpired = "Session expired.",
            unknownError = "An unexpected error has occurred."
        )
    )

    fun get(langCode: String): StudentDashboardContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}