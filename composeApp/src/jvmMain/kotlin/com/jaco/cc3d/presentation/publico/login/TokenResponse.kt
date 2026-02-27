package com.jaco.cc3d.presentation.publico.login

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    //var refreshToken: String,
    var token: String,
    //var name: String,
    //var driverId: String,
)