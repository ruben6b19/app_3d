package com.jaco.cc3d.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jaco.cc3d.data.local.entities.UserEntity

@Dao
interface UserDao {

    // Guarda la sesión. Si el usuario ya existe (ej. refrescó token), lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(user: UserEntity)

    // Obtiene la sesión actual. Solo debería haber una.
    @Query("SELECT * FROM session_user LIMIT 1")
    suspend fun getActiveSession(): UserEntity?

    // Borra todo al hacer Logout o si recibes un 'must_logout'
    @Query("DELETE FROM session_user")
    suspend fun clearSession()

    // Para actualizar solo el token cuando el interceptor lo refresque
    @Query("UPDATE session_user SET idToken = :newToken, expiresAt = :newExpiry WHERE mongoDbId = :userId")
    suspend fun updateToken(userId: String, newToken: String, newExpiry: Long)

    @Query("DELETE FROM session_user") // 👈 Esta es la magia de SQL
    suspend fun clearAll()
}