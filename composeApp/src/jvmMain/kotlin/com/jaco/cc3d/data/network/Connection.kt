package com.jaco.cc3d.data.network

import io.ktor.websocket.DefaultWebSocketSession

data class Connection(
    val session: DefaultWebSocketSession,
    val userId: String,    // Guardamos el ID único del estudiante
    val name: String,      // Nombre y Apellido combinados
    val address: String,
    val isTakingQuiz: Boolean = false
)