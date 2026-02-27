package com.jaco.cc3d.domain.repositories.cloudinary

import java.io.File

/**
 * Interfaz de Repositorio para la gestión de archivos multimedia en la nube.
 * Desacopla la lógica de negocio del SDK específico de Cloudinary.
 */
interface CloudinaryRepository {

    /**
     * Sube un archivo Markdown al almacenamiento.
     * @return Result con el public_id (String) si tiene éxito.
     */
    suspend fun uploadMarkdownFile(file: File): Result<String>

    /**
     * Elimina un archivo del almacenamiento.
     * @param publicId Referencia del archivo.
     */
    suspend fun deleteFile(publicId: String): Result<Unit>
}