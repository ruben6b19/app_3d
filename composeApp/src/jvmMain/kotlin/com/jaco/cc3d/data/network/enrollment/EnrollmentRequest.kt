package com.jaco.cc3d.data.network.enrollment

import kotlinx.serialization.Serializable

@Serializable
data class EnrollmentRequest(
    val student: String,
    val course: String
)