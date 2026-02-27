package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.network.institute.InstituteDto
import com.jaco.cc3d.data.network.institute.InstituteRequest
import com.jaco.cc3d.domain.models.Institute
import com.jaco.cc3d.domain.models.InstituteDomainRequest

fun InstituteDto.toDomainModel(): Institute {
    // Si tu DTO de Request lo requiere como String, el mapeo debe convertirlo a Int aquí
    // para el modelo de dominio, si es que el código de ciudad debe ser un Int en el dominio.
    //val cityAsInt = try { city.toInt() } catch (e: NumberFormatException) { 0 }

    return Institute(
        id = _id,
        name = name,
        foundationDate = foundationDate,
        city = city, // Mapeo y conversión (asumiendo que DTO.city es String pero representa un Int)
        language = language,
        status = status,
        usersCount = usersCount,
        coursesCount = coursesCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun InstituteDomainRequest.toDataRequest(): InstituteRequest {
    // La ciudad en el DomainRequest es String, lo cual coincide con lo que espera el DTO de la API.
    return InstituteRequest(
        name = this.name,
        foundationDate = this.foundationDate,
        city = this.city,
        language = this.language
    )
}