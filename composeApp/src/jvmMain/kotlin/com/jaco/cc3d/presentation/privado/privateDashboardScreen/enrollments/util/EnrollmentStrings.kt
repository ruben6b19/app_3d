package com.jaco.cc3d.presentation.privado.privateDashboardScreen.enrollments.util

import java.util.Locale

// 1. Data Class Padre
data class EnrollmentContent(
    val list: EnrollmentListStrings,
    val form: EnrollmentFormStrings,
    val feedback: EnrollmentFeedbackMessages
)

// 2. Sub-clases
data class EnrollmentListStrings(
    val titleScreen: String,
    val emptyListMessage: String,
    val retryButton: String,
    val fabEnroll: String, // Botón "Inscribir Alumno"
    val backButton: String,
    val studentNamePrefix: String,
    val studentEmailPrefix: String,
    val enrollmentDatePrefix: String,
    val removeAction: String, // "Desmatricular"
    val removeDialogTitle: String,
    val removeDialogMessage: (String) -> String,
    val cancelAction: String,
    val removeConfirmAction: String
)

data class EnrollmentFormStrings(
    val titleEnroll: String,
    val searchPlaceholder: String, // Buscar estudiante
    val buttonEnroll: String,
    val noStudentsFound: String,
    val studentLabel: String
)

data class EnrollmentFeedbackMessages(
    val requiredStudent: String,
    val sessionExpired: String,
    val unknownError: String,
    val successRemove: String,
    val successEnroll: String,
    val errorAlreadyEnrolled: String
)

// 3. Objeto Singleton con las traducciones
object EnrollmentResources {

    private val ES = EnrollmentContent(
        list = EnrollmentListStrings(
            titleScreen = "Gestión de Matrículas",
            emptyListMessage = "No hay estudiantes inscritos en este curso.",
            retryButton = "Reintentar Carga",
            fabEnroll = "Inscribir Alumno",
            backButton = "Volver a Cursos",
            studentNamePrefix = "Alumno:",
            studentEmailPrefix = "Email:",
            enrollmentDatePrefix = "Inscrito el:",
            removeAction = "Desmatricular",
            removeDialogTitle = "Confirmar Desmatriculación",
            removeDialogMessage = { name -> "¿Estás seguro de que deseas retirar a \"$name\" de este curso?" },
            cancelAction = "Cancelar",
            removeConfirmAction = "Retirar"
        ),
        form = EnrollmentFormStrings(
            titleEnroll = "Inscribir Nuevo Alumno",
            searchPlaceholder = "Buscar por nombre o email...",
            buttonEnroll = "Inscribir",
            noStudentsFound = "No se encontraron estudiantes disponibles.",
            studentLabel = "Seleccionar Estudiante"
        ),
        feedback = EnrollmentFeedbackMessages(
            requiredStudent = "Debes seleccionar un estudiante.",
            sessionExpired = "Sesión caducada.",
            unknownError = "Error desconocido.",
            successRemove = "Estudiante retirado del curso.",
            successEnroll = "Estudiante inscrito exitosamente.",
            errorAlreadyEnrolled = "Este estudiante ya está inscrito en el curso."
        )
    )

    private val EN = EnrollmentContent(
        list = EnrollmentListStrings(
            titleScreen = "Enrollment Management",
            emptyListMessage = "No students enrolled in this course.",
            retryButton = "Retry Load",
            fabEnroll = "Enroll Student",
            backButton = "Back to Courses",
            studentNamePrefix = "Student:",
            studentEmailPrefix = "Email:",
            enrollmentDatePrefix = "Enrolled on:",
            removeAction = "Unenroll",
            removeDialogTitle = "Confirm Unenrollment",
            removeDialogMessage = { name -> "Are you sure you want to remove \"$name\" from this course?" },
            cancelAction = "Cancel",
            removeConfirmAction = "Remove"
        ),
        form = EnrollmentFormStrings(
            titleEnroll = "Enroll New Student",
            searchPlaceholder = "Search by name or email...",
            buttonEnroll = "Enroll",
            noStudentsFound = "No available students found.",
            studentLabel = "Select Student"
        ),
        feedback = EnrollmentFeedbackMessages(
            requiredStudent = "You must select a student.",
            sessionExpired = "Session expired.",
            unknownError = "Unknown error.",
            successRemove = "Student removed from course.",
            successEnroll = "Student enrolled successfully.",
            errorAlreadyEnrolled = "This student is already enrolled in the course."
        )
    )

    fun get(langCode: String): EnrollmentContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}