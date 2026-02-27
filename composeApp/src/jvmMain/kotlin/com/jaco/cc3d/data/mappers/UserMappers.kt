package com.jaco.cc3d.data.mappers

import com.jaco.cc3d.data.network.user.UserDto
import com.jaco.cc3d.data.network.user.UserRequest
import com.jaco.cc3d.domain.models.User
import com.jaco.cc3d.domain.models.UserDomainRequest

/**
 * Extensión para convertir un UserDto (Data/Network) a un User (Domain Model).
 */
fun UserDto.toDomainModel(): User {

    return User(
        id = _id,
        fullName = fullName,
        email = email,
        firebaseUid = firebaseUid,
        // Nota: Asumimos que la lista de roles y el estado son ya Ints en el DTO,
        // o que la lógica de la API devuelve el tipo esperado por el modelo de Dominio (List<Int> y Int).
        role = role,
        status = status,
        instituteId = institute,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Extensión para convertir un UserDomainRequest (Domain) a un UserRequest (Data/Network).
 */
fun UserDomainRequest.toDataRequest(): UserRequest {
    return UserRequest(
        fullName = this.fullName,
        email = this.email,
        // Al igual que arriba, asumimos que el Request de Dominio ya maneja los tipos
        // que el Request de la API (UserRequest) espera.
        role = this.role,
        status = this.status,
        institute = this.instituteId,
        // Contraseñas (password) solo se envían al crear/actualizar
        password = this.password
    )
}