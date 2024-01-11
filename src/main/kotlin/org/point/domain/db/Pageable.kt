package org.point.domain.db

import org.point.service.PaginationUtils

data class Pageable(
    val offset: Int,
    val limit: Int,
    val orderBy: String?,
    val sortOrder: SortDirection
) {

    constructor(
        page: Int? = 1,
        limit: Int? = PaginationUtils.defaultLimit,
        sortBy: Enum<*>? = null,
        sortDirection: String? = null
    ) :
            this(
                offset = PaginationUtils.getOffset(page, limit),
                limit = limit ?: PaginationUtils.defaultLimit,
                orderBy = sortBy?.name,
                sortOrder = sortDirection?.let { SortDirection.valueOf(sortDirection) } ?: SortDirection.ASC
            )

    constructor(
        page: Int? = 1,
        limit: Int? = PaginationUtils.defaultLimit,
        sortBy: String? = null,
        sortDirection: String? = null
    ) :
            this(
                offset = PaginationUtils.getOffset(page, limit),
                limit = limit ?: PaginationUtils.defaultLimit,
                orderBy = sortBy,
                sortOrder = sortDirection?.let { SortDirection.valueOf(sortDirection) } ?: SortDirection.ASC
            )
}