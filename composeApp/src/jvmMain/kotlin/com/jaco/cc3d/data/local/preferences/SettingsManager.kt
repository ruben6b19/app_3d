package com.jaco.cc3d.data.local.preferences

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class AppSettings(val fontSize: Float = 37f)

object SettingsManager {
    private val folder = File(System.getProperty("user.home"), ".cc3d_data")
    private val settingsFile = File(folder, "settings.json")
    private val json = Json { ignoreUnknownKeys = true }

    fun save(fontSize: Float) {
        try {
            if (!folder.exists()) folder.mkdirs()
            val data = AppSettings(fontSize)
            settingsFile.writeText(json.encodeToString(AppSettings.serializer(), data))
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun load(): AppSettings {
        return try {
            if (settingsFile.exists()) {
                json.decodeFromString(AppSettings.serializer(), settingsFile.readText())
            } else AppSettings()
        } catch (e: Exception) { AppSettings() }
    }
}