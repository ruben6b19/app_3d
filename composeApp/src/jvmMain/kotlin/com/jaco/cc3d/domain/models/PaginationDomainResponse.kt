package com.jaco.cc3d.domain.models

data class PaginationDomainResponse<T>(
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