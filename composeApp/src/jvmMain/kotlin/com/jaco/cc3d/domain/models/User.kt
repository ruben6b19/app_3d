package com.jaco.cc3d.domain.models

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val firebaseUid: String?, // ID de Firebase si existe
    val role: List<Int>, // 0: student, 1: teacher, 2: admin
    val status: Int, // 0: inactive, 1: active, 2: banned
    val instituteId: String, // ID del instituto al que pertenece
    val createdAt: String, // Fecha de creación (ISO Date)
    val updatedAt: String, // Fecha de actualización (ISO Date)
)