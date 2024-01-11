package org.point.domain

import java.util.UUID

data class Favourites(
    val id: Int,
    val userId: UUID,
    val performerId: UUID,
    val name: String?,
)
