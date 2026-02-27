package com.jaco.cc3d.data.network.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val success: Boolean,
    val statusCode: Int,
    val message: String
)