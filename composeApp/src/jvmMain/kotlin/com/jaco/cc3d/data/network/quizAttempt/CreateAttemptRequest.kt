package com.jaco.cc3d.data.network.quizAttempt

import kotlinx.serialization.Serializable

@Serializable
data class CreateAttemptRequest(
    val scheduledQuizId: String,
    val amount: Int = 10,
    val isRandom: Boolean = true
)