package com.jaco.cc3d.domain.models

data class UserDomainRequest(
    val email: String,
    val fullName: String,
    val role: List<Int>,
    val instituteId: String, // Requerido para crear y actualizar
    val status: Int = 1, // Por defecto Activo
    val password: String
)