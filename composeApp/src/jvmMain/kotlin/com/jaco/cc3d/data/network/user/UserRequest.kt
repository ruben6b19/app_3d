package com.jaco.cc3d.data.network.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val email: String,
    val fullName: String,
    val role: List<Int>,
    val status: Int, // Incluimos status para crearlo/actualizarlo
    val institute: String, // ID del instituto para asignación (CRUCIAL)
    val password: String? = null // Opcional, solo si se crea o se actualiza la contraseña
)