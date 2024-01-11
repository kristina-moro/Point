package org.point.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Request(
    val id: UUID,
    val type: String,
    val userId: UUID,
    val userName: String,
    val login: String,
    val password: String,
    val validFrom: OffsetDateTime,
    val validTo: OffsetDateTime
)
