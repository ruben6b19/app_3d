package com.jaco.cc3d.data.network.apiAuth

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRequest(
    val idToken: String,
    val refreshToken : String
)