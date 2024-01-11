package org.point.domain

import java.time.OffsetDateTime
import java.util.UUID

data class EventLogItem(
    val id: Int,
    val userId: UUID,
    val eventType: EventType,
    val note: String?,
    val datetime: OffsetDateTime
)
