package com.jaco.cc3d.domain.usecases.subjectConfig

import com.jaco.cc3d.domain.models.Slide
import com.jaco.cc3d.domain.models.SubjectConfig
import com.jaco.cc3d.domain.models.SubjectConfigDomainRequest
import java.io.File
import javax.inject.Inject

/**
 * Orquestador: Coordina la subida a Cloudinary y la persistencia en MongoDB.
 */
class SyncSubjectContent @Inject constructor(
    private val uploadUseCase: UploadSubjectContent,
    private val saveConfigUseCase: SaveSubjectConfig
) {
    suspend operator fun invoke(
        file: File,
        subjectId: String,
        slides: List<Slide>,
        version: String = "1.0.0",
        commitMessage: String? = null
    ): Result<SubjectConfig> {

        // Paso 1: Subir el archivo .md a Cloudinary
        val uploadResult = uploadUseCase(file)

        // Si la subida falla, cortamos el flujo y devolvemos el error
        val cloudinaryId = uploadResult.getOrElse {
            return Result.failure(it)
        }

        // Paso 2: Preparar la petición para el backend de Node.js
        val configRequest = SubjectConfigDomainRequest(
            subjectId = subjectId,
            contentUrl = cloudinaryId, // El ID que nos devolvió Cloudinary
            slides = slides,
            version = version,
            lastCommitMessage = commitMessage ?: "Sincronización automática: ${file.name}"
        )

        // Paso 3: Guardar en la base de datos
        return saveConfigUseCase(configRequest)
    }
}