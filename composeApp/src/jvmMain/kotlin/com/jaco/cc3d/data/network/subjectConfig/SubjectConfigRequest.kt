package com.jaco.cc3d.data.network.subjectConfig


import kotlinx.serialization.Serializable

@Serializable
data class SubjectConfigRequest(
    val subjectId: String,
    val contentUrl: String, // Aquí envías el ID que te dio Cloudinary
    val slides: List<SlideDto> = emptyList(),
    val version: String = "1.0.0",
    val lastCommitMessage: String? = null,
    val contentHash: String? = null
)