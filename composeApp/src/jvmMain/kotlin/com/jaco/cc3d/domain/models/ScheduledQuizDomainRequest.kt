package com.jaco.cc3d.domain.models

/**
 * Modelo de Dominio para la solicitud de creación o edición de un Quiz Programado.
 * Mapea los datos necesarios desde la UI hacia la capa de datos.
 */
data class ScheduledQuizDomainRequest(
    val courseId: String,
    val quizTemplateId: String,
    val quizDate: String,
    val details: String? = null,
    val status: Int = 1
)