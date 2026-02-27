package com.jaco.cc3d.domain.models

data class Institute(
    val id: String, // Usamos 'id' en lugar de '_id' (típico de MongoDB)
    val name: String,
    val foundationDate: String, // Mantener como String (ISO 8601) para simplificar el dominio
    val city: Int, // Asumimos que 'city' es un código numérico de dominio (e.g., código postal, código de ciudad)
    val language: String, // Usamos 'languageCode' en lugar de 'language' para mayor claridad
    val status: Int,
    val usersCount: Int,
    val coursesCount: Int,
    val createdAt: String,
    val updatedAt: String
)

