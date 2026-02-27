package com.jaco.cc3d.data.network.subject

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) para la entidad Materia.
 * Representa la estructura de datos que se recibe desde el API/MongoDB.
 */
@Serializable
data class SubjectDto(
    val _id: String,
    val name: String,
    val description: String? = null,
    val createdBy: String,
    val updatedBy: String? = null,
    val status: Int = 1,
    val createdAt: String,
    val updatedAt: String
)