package com.jaco.cc3d.data.network.institute

import kotlinx.serialization.Serializable

@Serializable
data class InstituteRequest(
    val name: String,
    val foundationDate: String,
    val city: Int,
    val language: String
)