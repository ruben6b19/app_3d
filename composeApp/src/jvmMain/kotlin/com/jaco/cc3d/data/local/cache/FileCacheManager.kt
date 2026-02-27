package com.jaco.cc3d.data.local.cache

import java.io.File

object FileCacheManager {
    // Carpeta persistente: C:\Users\Nombre\.cc3d_data (Windows) o /home/nombre/.cc3d_data (Linux/Mac)
    private val rootDir = File(System.getProperty("user.home"), ".cc3d_data").apply {
        if (!exists()) mkdirs()
    }

    private val mdCacheDir = File(rootDir, "markdown_cache").apply {
        if (!exists()) mkdirs()
    }

    fun isDownloaded(subjectId: String, contentUrl: String?): Boolean {
        if (contentUrl.isNullOrBlank()) return false
        val fileName = "${subjectId}__${contentUrl.replace("/", "_")}"
        return File(mdCacheDir, fileName).exists()
    }
    /**
     * Busca un archivo que coincida con la combinación de materia y UID de Cloudinary.
     */
    fun getCachedFile(subjectId: String, contentId: String): String? {
        val fileName = "${subjectId}__${cleanName(contentId)}"
        val file = File(mdCacheDir, fileName)
        return if (file.exists()) file.readText() else null
    }

    /**
     * Guarda el nuevo MD y ELIMINA cualquier versión anterior de la misma materia.
     */
    fun saveToCache(subjectId: String, contentId: String, content: String) {
        // 1. Limpieza de huérfanos: Borrar cualquier archivo que empiece con "subjectId__"
        mdCacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("${subjectId}__")) {
                file.delete()
                println("Base de datos local: Versión antigua eliminada para $subjectId")
            }
        }

        // 2. Guardar la nueva versión
        val newFileName = "${subjectId}__${cleanName(contentId)}"
        File(mdCacheDir, newFileName).writeText(content)
    }

    private fun cleanName(name: String) = name.replace("/", "_").replace("\\", "_")


    /**
     * 🗑️ Función para que el usuario pueda limpiar la caché manualmente si lo desea.
     */
    fun clearAllCache() {
        mdCacheDir.deleteRecursively()
        mdCacheDir.mkdirs()
    }

    private fun cleanFileName(name: String) = name.replace("/", "_").replace("\\", "_")
}