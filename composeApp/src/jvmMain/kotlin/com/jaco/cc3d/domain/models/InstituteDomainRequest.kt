package com.jaco.cc3d.domain.models

data class InstituteDomainRequest(
    val name: String,
    val foundationDate: String,
    val city: Int, // Se mantiene como String/Number según lo que espera el DTO/backend
    val language: String
)