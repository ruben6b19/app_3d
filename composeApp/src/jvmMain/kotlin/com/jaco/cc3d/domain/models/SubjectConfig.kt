package com.jaco.cc3d.domain.models

data class SubjectConfig(
    val id: String?,
    val subject: String,
    val contentUrl: String,
    val slides: List<Slide>,
    val version: String,
    val lastCommitMessage: String?
)

data class Slide(
    val slideId: String,
    val label: String?,
    val type: String
)

// Request para el Use Case
data class SubjectConfigDomainRequest(
    val subjectId: String,
    val contentUrl: String,
    val slides: List<Slide> = emptyList(),
    val version: String = "1.0.0",
    val lastCommitMessage: String? = null,
    val contentHash: String? = null
)