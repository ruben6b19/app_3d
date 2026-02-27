package com.jaco.cc3d.data.network.common

import kotlinx.serialization.Serializable
/**
 * Estructura de paginación basada en mongoose-paginate-v2.
 */
@Serializable
data class PaginationResponse<T>(
    val docs: List<T>,
    val totalDocs: Int,
    val limit: Int,
    val totalPages: Int,
    val page: Int?,
    val pagingCounter: Int,
    val hasPrevPage: Boolean,
    val hasNextPage: Boolean,
    val prevPage: Int?,
    val nextPage: Int?
)
