package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.network.subject.SubjectDto
import com.jaco.cc3d.data.network.subject.SubjectRequest
import com.jaco.cc3d.domain.models.Subject
import com.jaco.cc3d.domain.models.SubjectDomainRequest

/**
 * ----------------------------------------------------
 * Mapeo de DTO (Data) a Modelo de Dominio (Domain)
 * ----------------------------------------------------
 * Convierte el objeto de red (SubjectDto) al modelo de negocio puro (Subject).
 */
fun SubjectDto.toDomainModel(): Subject {
    return Subject(
        id = this._id,
        name = this.name,
        description = this.description,
        createdBy = this.createdBy,
        updatedBy = this.updatedBy,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * ----------------------------------------------------
 * Mapeo de Request de Dominio a Request de Datos
 * ----------------------------------------------------
 * Convierte la solicitud de la UI/Domain (SubjectDomainRequest) al objeto
 * que espera la API (SubjectRequest).
 * NOTA: Los campos de auditoría (createdBy, createdAt) no se incluyen ya que
 * son manejados por el servidor.
 */
fun SubjectDomainRequest.toDataRequest(): SubjectRequest {
    return SubjectRequest(
        name = this.name,
        description = this.description
    )
}