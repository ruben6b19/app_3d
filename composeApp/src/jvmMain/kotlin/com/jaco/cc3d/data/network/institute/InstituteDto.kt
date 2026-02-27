package com.jaco.cc3d.data.network.institute

import kotlinx.serialization.Serializable

@Serializable
data class InstituteDto(
    val _id: String,
    val name: String,
    val foundationDate: String,
    val city: Int,
    val language: String,
    val status: Int,
    val usersCount: Int,
    val coursesCount: Int,
    val createdAt: String,
    val updatedAt: String
)