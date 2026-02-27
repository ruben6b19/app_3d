package com.jaco.cc3d.data.network.course

import kotlinx.serialization.Serializable

/**
 * Representa el cuerpo de la solicitud (Body) para crear o actualizar un Curso.
 * El ID del creador (createdBy) se asume que se obtendrá del token de sesión en el backend.
 * El updatedBy no se incluye ya que se gestiona automáticamente en el PATCH en el servidor.
 */
@Serializable
data class CourseRequest(
    val institute: String,
    val subject: String,
    val teacher: String,
    val academicYear: Int,
    val group: String
)