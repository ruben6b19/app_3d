package com.jaco.cc3d.data.remote.cloudinary

import com.cloudinary.utils.ObjectUtils
import com.jaco.cc3d.data.network.config.CloudinaryClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryService @Inject constructor() {

    private val cloudinary = CloudinaryClient.instance

    /**
     * Sube un archivo Markdown (.md) a Cloudinary.
     * @param file El archivo local seleccionado desde el Desktop.
     * @return El public_id del archivo en Cloudinary o null si falla.
     */
    suspend fun uploadMarkdownFile(file: File): String? = withContext(Dispatchers.IO) {
        try {
            // Configuramos la subida
            val params = ObjectUtils.asMap(
                "resource_type", "raw",         // Crucial para archivos .md
                "upload_preset", "lcqwtgjb",
            )

            val uploadResult = cloudinary.uploader().upload(file, params)
            val fullPublicId = uploadResult["public_id"] as? String

            fullPublicId?.substringAfterLast("/")
        } catch (e: Exception) {
            println("Cloudinary Error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Elimina un archivo de Cloudinary si es necesario (ej: al borrar una materia).
     */
    suspend fun deleteFile(publicId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"))
            result["result"] == "ok"
        } catch (e: Exception) {
            false
        }
    }
}