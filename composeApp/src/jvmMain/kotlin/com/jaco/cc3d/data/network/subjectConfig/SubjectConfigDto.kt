package com.jaco.cc3d.data.network.subjectConfig

import kotlinx.serialization.Serializable

@Serializable
data class SubjectConfigDto(
    val id: String? = null, // ID del documento de configuración
    val subject: String,
    val contentUrl: String,
    val slides: List<SlideDto>,
    val version: String,
    val contentHash: String? = "",
    val lastCommitMessage: String? = "",
    val updatedAt: String? = null
)

