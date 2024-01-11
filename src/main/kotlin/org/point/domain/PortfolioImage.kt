package org.point.domain

import java.util.UUID

data class PortfolioImage(
    val userId: UUID,
    val id: Int,
    val description: String? = null,
    val sortOrder: Int? = null,
    val data: ByteArray,
)
