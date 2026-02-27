package com.jaco.cc3d.presentation.privado.teacherDashboard.util

import java.util.Locale

data class TeacherDashboardContent(
    val welcome: TeacherWelcomeStrings,
    val list: TeacherCourseListStrings,
    val feedback: TeacherDashboardFeedback
)

data class TeacherWelcomeStrings(
    val greeting: (String) -> String,
    val subtitle: String,
    val titleScreen: String
)

data class TeacherCourseListStrings(
    val courseTitle: String,
    val studentsCount: (Int) -> String,
    val emptyMessage: String,
    val retryButton: String,
    val unknownSubject: String
)

data class TeacherDashboardFeedback(
    val loading: String,
    val loadError: String,
    val sessionExpired: String,
    val unknownError: String
)

object TeacherDashboardResources {

    private val ES = TeacherDashboardContent(
        welcome = TeacherWelcomeStrings(
            greeting = { name -> "¡Buen día, Prof. $name!" },
            subtitle = "Estos son los cursos que impartes en este periodo.",
            titleScreen = "Mis Cursos"
        ),
        list = TeacherCourseListStrings(
            courseTitle = "Curso:",
            studentsCount = { count -> "$count alumnos inscritos" },
            emptyMessage = "No tienes cursos asignados para este periodo.",
            retryButton = "Reintentar",
            unknownSubject = "Materia no definida"
        ),
        feedback = TeacherDashboardFeedback(
            loading = "Cargando tus cursos...",
            loadError = "Error al cargar los cursos",
            sessionExpired = "La sesión ha expirado.",
            unknownError = "Ha ocurrido un error inesperado."
        )
    )

    private val EN = TeacherDashboardContent(
        welcome = TeacherWelcomeStrings(
            greeting = { name -> "Good day, Prof. $name!" },
            subtitle = "These are the courses you are teaching this period.",
            titleScreen = "My Courses"
        ),
        list = TeacherCourseListStrings(
            courseTitle = "Course:",
            studentsCount = { count -> "$count students enrolled" },
            emptyMessage = "No courses assigned for this period.",
            retryButton = "Retry",
            unknownSubject = "Undefined Subject"
        ),
        feedback = TeacherDashboardFeedback(
            loading = "Loading your courses...",
            loadError = "Error loading courses",
            sessionExpired = "Session expired.",
            unknownError = "An unexpected error has occurred."
        )
    )

    fun get(langCode: String): TeacherDashboardContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}