package com.jaco.cc3d.data.repositories.cloudinary

import com.jaco.cc3d.data.remote.cloudinary.CloudinaryService
import com.jaco.cc3d.domain.repositories.cloudinary.CloudinaryRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryRepositoryImpl @Inject constructor(
    private val cloudinaryService: CloudinaryService
) : CloudinaryRepository {

    override suspend fun uploadMarkdownFile(file: File): Result<String> {
        val publicId = cloudinaryService.uploadMarkdownFile(file)
        return if (publicId != null) {
            Result.success(publicId)
        } else {
            Result.failure(Exception("Error al subir el archivo Markdown a Cloudinary"))
        }
    }

    override suspend fun deleteFile(publicId: String): Result<Unit> {
        val success = cloudinaryService.deleteFile(publicId)
        return if (success) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("No se pudo eliminar el archivo de Cloudinary"))
        }
    }
}