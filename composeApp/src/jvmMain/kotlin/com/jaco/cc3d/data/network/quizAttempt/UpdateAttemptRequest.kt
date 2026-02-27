package com.jaco.cc3d.data.network.quizAttempt

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAttemptRequest(
    val answers: Map<String, Int>, // ID Pregunta -> Índice respuesta
    val isFinalSubmit: Boolean = false
)