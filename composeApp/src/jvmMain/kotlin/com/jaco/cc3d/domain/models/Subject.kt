package com.jaco.cc3d.domain.models

data class Subject(
    val id: String,
    val name: String,
    val description: String? = null, // Opcional
    val createdBy: String,
    val updatedBy: String? = null, // Opcional
    val status: Int = 1,
    val createdAt: String,
    val updatedAt: String
)