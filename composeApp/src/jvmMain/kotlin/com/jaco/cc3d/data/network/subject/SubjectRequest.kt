package com.jaco.cc3d.data.network.subject

import kotlinx.serialization.Serializable

/**
 * Objeto de Solicitud (Request) para crear o actualizar una Materia.
 */
@Serializable
data class SubjectRequest(
    val name: String,
    val description: String? = null,
)