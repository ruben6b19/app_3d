package com.jaco.cc3d.domain.repositories.subjectConfig

import com.jaco.cc3d.data.network.subjectConfig.SubjectConfigDto
import com.jaco.cc3d.data.network.subjectConfig.SubjectConfigRequest

/**
 * Interfaz de Repositorio para la configuración del contenido (Markdown/Cloudinary).
 * Maneja la persistencia de los IDs de diapositivas y la referencia al archivo.
 */
interface SubjectConfigRepository {

    /**
     * Guarda o actualiza la configuración de la materia.
     * Vincula el ID de Cloudinary (contentUrl) y el mapa de diapositivas a la materia.
     */
    suspend fun saveSubjectConfig(request: SubjectConfigRequest): Result<SubjectConfigDto>

    /**
     * Recupera la configuración actual de una materia.
     * @param subjectId ID de la materia en MongoDB.
     */
    suspend fun getSubjectConfig(subjectId: String): Result<SubjectConfigDto>
}