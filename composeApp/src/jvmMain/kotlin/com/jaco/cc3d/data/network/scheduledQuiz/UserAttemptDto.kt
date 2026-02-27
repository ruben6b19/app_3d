package com.jaco.cc3d.data.network.scheduledQuiz

import kotlinx.serialization.Serializable

@Serializable
data class UserAttemptDto(
    val hasAttempted: Boolean,
    val status: Int?,
    val score: Double
)