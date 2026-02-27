package com.jaco.cc3d.presentation.privado.privateDashboardScreen.courses.util

import java.util.Locale

// 1. Data Class Padre
data class CoursesContent(
    val list: CourseListStrings,
    val form: CourseFormStrings,
    val feedback: CourseFeedbackMessages
)

// 2. Sub-clases
data class CourseListStrings(
    val titleScreen: String,
    val emptyListMessage: String,
    val retryButton: String,
    val fabCreate: String,
    val backButton: String,
    val institutePrefix: String,
    val subjectPrefix: String,
    val teacherPrefix: String,
    val studentsPrefix: String,
    val yearPrefix: String,
    val groupPrefix: String,
    val editAction: String,
    val deleteAction: String,
    val deleteDialogTitle: String,
    val deleteDialogMessage: (String) -> String,
    val cancelAction: String,
    val deleteConfirmAction: String
)

data class CourseFormStrings(
    val titleRegister: String,
    val titleEdit: (String) -> String,
    val buttonCreate: String,
    val buttonSave: String,
    val fieldSubject: String,
    val fieldTeacher: String,
    val fieldAcademicYear: String,
    val fieldGroup: String,
    val fieldInstitute: String,
    val success: String
)

data class CourseFeedbackMessages(
    val requiredInstitute: String,
    val requiredSubject: String,
    val requiredTeacher: String,
    val requiredYear: String,
    val invalidYear: String,
    val requiredGroup: String,
    val formError: String,
    val sessionExpired: String,
    val unknownError: String,
    val noCourseSelected: String,
    val successDelete: String,
    val successCreate: String,
    val successUpdate: String
)

// 3. Objeto Singleton con las traducciones
object CoursesResources {

    private val ES = CoursesContent(
        list = CourseListStrings(
            titleScreen = "Gestión de Cursos",
            emptyListMessage = "No hay cursos registrados.",
            retryButton = "Reintentar Carga",
            fabCreate = "Crear Nuevo Curso",
            backButton = "Volver",
            institutePrefix = "Instituto:",
            subjectPrefix = "Materia:",
            teacherPrefix = "Profesor:",
            studentsPrefix = "Alumnos:",
            yearPrefix = "Año Académico:",
            groupPrefix = "Grupo:",
            editAction = "Editar",
            deleteAction = "Eliminar",
            deleteDialogTitle = "Confirmar Eliminación",
            deleteDialogMessage = { id -> "¿Estás seguro de que quieres eliminar el curso con ID \"$id\" de forma permanente?" },
            cancelAction = "Cancelar",
            deleteConfirmAction = "Eliminar"
        ),
        form = CourseFormStrings(
            titleRegister = "Registrar Nuevo Curso",
            titleEdit = { id -> "Editar Curso: $id" },
            buttonCreate = "Crear Curso",
            buttonSave = "Guardar Cambios",
            fieldSubject = "Materia",
            fieldTeacher = "Profesor",
            fieldAcademicYear = "Año Académico (YYYY)",
            fieldGroup = "Grupo",
            fieldInstitute = "Instituto (ID)",
            success = "¡Operación exitosa!"
        ),
        feedback = CourseFeedbackMessages(
            requiredInstitute = "El instituto es obligatorio.",
            requiredSubject = "La materia es obligatoria.",
            requiredTeacher = "El profesor es obligatorio.",
            requiredYear = "El año académico es obligatorio.",
            invalidYear = "El año académico debe ser un año válido (YYYY).",
            requiredGroup = "El grupo es obligatorio.",
            formError = "Corrige los errores en el formulario.",
            sessionExpired = "Sesión caducada. Por favor, vuelve a iniciar sesión.",
            unknownError = "Error desconocido.",
            noCourseSelected = "No se ha seleccionado un curso para editar.",
            successDelete = "Curso eliminado exitosamente.",
            successCreate = "Curso creado exitosamente.",
            successUpdate = "Curso actualizado exitosamente."
        )
    )

    private val EN = CoursesContent(
        list = CourseListStrings(
            titleScreen = "Course Management",
            emptyListMessage = "No registered courses found.",
            retryButton = "Retry Load",
            fabCreate = "Create New Course",
            backButton = "Go Back",
            institutePrefix = "Institute:",
            subjectPrefix = "Subject:",
            teacherPrefix = "Teacher:",
            studentsPrefix = "Students:",
            yearPrefix = "Academic Year:",
            groupPrefix = "Group:",
            editAction = "Edit",
            deleteAction = "Delete",
            deleteDialogTitle = "Confirm Deletion",
            deleteDialogMessage = { id -> "Are you sure you want to permanently delete course with ID \"$id\"?" },
            cancelAction = "Cancel",
            deleteConfirmAction = "Delete"
        ),
        form = CourseFormStrings(
            titleRegister = "Register New Course",
            titleEdit = { id -> "Edit Course: $id" },
            buttonCreate = "Create Course",
            buttonSave = "Save Changes",
            fieldSubject = "Subject",
            fieldTeacher = "Teacher",
            fieldAcademicYear = "Academic Year (YYYY)",
            fieldGroup = "Group",
            // 🆕 Traducción en EN
            fieldInstitute = "Institute (ID)",
            success = "Operation successful!"
        ),
        feedback = CourseFeedbackMessages(
            // 🆕 Traducción en EN
            requiredInstitute = "The institute is mandatory.",
            requiredSubject = "The subject is mandatory.",
            requiredTeacher = "The teacher is mandatory.",
            requiredYear = "The academic year is mandatory.",
            invalidYear = "The academic year must be a valid year (YYYY).",
            requiredGroup = "The group is mandatory.",
            formError = "Please correct the form errors.",
            sessionExpired = "Session expired. Please log in again.",
            unknownError = "Unknown error.",
            noCourseSelected = "No course selected for editing.",
            successDelete = "Course deleted successfully.",
            successCreate = "Course created successfully.",
            successUpdate = "Course updated successfully."
        )
    )

    fun get(langCode: String): CoursesContent {
        return when (langCode.lowercase(Locale.ROOT)) {
            "es" -> ES
            else -> EN
        }
    }
}