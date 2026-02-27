package com.jaco.cc3d.domain.usecases.subjectConfig

import com.jaco.cc3d.domain.repositories.cloudinary.CloudinaryRepository
import java.io.File
import javax.inject.Inject

/**
 * Use Case encargado de la lógica de subida de archivos Markdown.
 * Recibe un archivo físico del sistema y retorna el ID de Cloudinary.
 */
class UploadSubjectContent @Inject constructor(
    private val cloudinaryRepository: CloudinaryRepository
) {
    suspend operator fun invoke(file: File): Result<String> {
        // Podrías añadir validaciones de negocio aquí,
        // por ejemplo: verificar que la extensión sea .md
        if (file.extension.lowercase() != "md") {
            return Result.failure(Exception("El archivo debe ser formato Markdown (.md)"))
        }

        return cloudinaryRepository.uploadMarkdownFile(file)
    }
}