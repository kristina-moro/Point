package org.point.service

import org.point.domain.EventLogItem
import org.point.domain.EventType
import org.point.domain.db.Pageable
import org.point.repository.EventLogRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class EventLogService(private val repository: EventLogRepository) {

    fun getTotal(userId: UUID): Int {
        val total = repository.findTotalByUserId(userId)
        return repository.findTotalByUserId(userId)
    }

    fun getLog(userId: UUID, pageable: Pageable): List<EventLogItem> {
        return repository.findByUserId(userId, pageable)
    }

    fun logEvent(
        userId: UUID,
        eventType: EventType,
        note: String? = null,
        datetime: OffsetDateTime? = null
    ) {
        repository.logEvent(
            userId = userId,
            eventType = eventType.name,
            note = note,
            datetime = datetime
        )
    }

    /*fun logEvent(
        userId: UUID,
        eventType: EventType,
        note: String? = null
    ) {
        repository.logEvent(
            userId = userId,
            eventType = eventType.name,
            note = note
        )
    }*/
}