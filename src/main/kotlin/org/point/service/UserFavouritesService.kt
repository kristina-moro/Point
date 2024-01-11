package org.point.service

import org.point.domain.EventType
import org.point.domain.Favourites
import org.point.repository.FavouritesRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserFavouritesService(
    private val favouritesRepository: FavouritesRepository,
    private val eventLogService: EventLogService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun addFavourites(userId: UUID, performerId: UUID, name: String? = null): Int {
        val res = favouritesRepository.add(userId, performerId, name)
        if (res == 1) {
            eventLogService.logEvent(userId = userId, eventType = EventType.ADDED_FAVOURITES, note = "$performerId")
        }
        return res
    }

    fun deleteFavourites(userId: UUID, performerId: UUID): Int {
        val res = favouritesRepository.delete(userId, performerId)
        if (res == 1) {
            eventLogService.logEvent(userId = userId, eventType = EventType.REMOVED_FAVOURITES, note = "$performerId")
        }
        return res
    }

    fun getFavourites(userId: UUID): List<Favourites> {
        return favouritesRepository.findByUser(userId)
    }
}