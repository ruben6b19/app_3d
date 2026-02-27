package com.jaco.cc3d.data.network.user

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val _id: String,
    val fullName: String, // Este sí llega
    val email: String = "", // 👈 Valor por defecto si no viene en el JSON
    val firebaseUid: String? = null,
    val role: List<Int> = emptyList(), // 👈 Lista vacía por defecto
    val status: Int = 1, // 👈 Valor por defecto
    val createdBy: String = "",
    val institute: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)