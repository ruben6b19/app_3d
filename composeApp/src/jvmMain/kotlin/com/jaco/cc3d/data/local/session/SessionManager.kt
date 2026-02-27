package com.jaco.cc3d.data.local.session

import com.jaco.cc3d.data.local.dao.UserDao
import jakarta.inject.Inject

// Ubicación: com.jaco.cc3d.data.local.session.SessionManager
class SessionManager @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun isSessionValid(): Boolean {
        val session = userDao.getActiveSession() ?: return false

        // Obtenemos la hora actual
        val currentTime = System.currentTimeMillis()

        // Margen de seguridad: Si faltan menos de 5 minutos para expirar,
        // la consideramos inválida para forzar un refresh.
        val bufferTime = 5 * 60 * 1000 // 5 minutos en ms

        return currentTime < (session.expiresAt - bufferTime)
    }

    suspend fun getRemainingTimeMinutes(): Long {
        val session = userDao.getActiveSession() ?: return 0
        val diff = session.expiresAt - System.currentTimeMillis()
        return if (diff > 0) diff / 1000 / 60 else 0
    }
}