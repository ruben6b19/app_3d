package com.jaco.cc3d.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_user")
data class UserEntity(
    @PrimaryKey
    val mongoDbId: String,
    val name: String,
    val email: String,
    val idToken: String,
    val refreshToken: String,
    val expiresAt: Long,       // Timestamp (ms) devuelto por el backend
    val role: String,          // Guardamos el array como String (ej: "1,0")
    val instituteId: String?,  // Opcional por si es Admin
    val language: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)