package org.point.service

import org.point.domain.db.Pageable

class PaginationUtils {
    companion object {
        const val defaultLimit = 20
        fun getOffset(page: Int?, limit: Int?): Int {
            ValueValidationService.validatePositive(propertyName = "page", value = page)
            ValueValidationService.validatePositive(propertyName = "limit", value = limit)

            page?.let {
                return (it - 1) * (limit ?: defaultLimit)
            }
            return 0
        }

        fun Pageable.getSql(): String {
            if (orderBy == null) {
                return " LIMIT $limit OFFSET $offset"
            }
            return " ORDER BY ${orderBy} ${sortOrder.name} LIMIT $limit OFFSET $offset"
        }

    }
}