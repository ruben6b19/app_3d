package com.jaco.cc3d.data.network.subjectConfig

import kotlinx.serialization.Serializable

@Serializable
data class SlideDto(
    val slideId: String,
    val label: String? = "",
    val type: String = "text"
)