package com.jaco.cc3d.data.network.common

import kotlinx.serialization.Serializable

@Serializable
data class BackendResponseWrapper<T>(
    val statusCode: Int,
    val data: T?,
    val message: String,
    val success: Boolean
)