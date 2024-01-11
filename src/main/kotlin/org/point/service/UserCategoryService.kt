package org.point.service

import org.point.domain.Category
import org.point.domain.EventType
import org.point.repository.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserCategoryService(
    private val repository: CategoryRepository,
    private val eventLogService: EventLogService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getUserCategory(userId: UUID): List<Category> {
        return repository.getUserCategory(userId)
    }

    fun addCategory(userId: UUID, categoryId: Int): Int {
        repository.getCategory(categoryId)?.let { cat ->
            return repository.addUserCategory(userId, categoryId).also {
                if (it == 1) {
                    eventLogService.logEvent(
                        userId = userId,
                        eventType = EventType.ADDED_CATEGORY,
                        """{ name: ${cat.name}}"""
                    )
                }
            }
        }
        return 0
    }

    fun removeCategory(userId: UUID, categoryId: Int): Int {
        repository.getCategory(categoryId)?.let { cat ->
            return repository.removeUserCategory(userId, categoryId).also {
                if (it == 1) {
                    eventLogService.logEvent(
                        userId = userId,
                        eventType = EventType.REMOVED_CATEGORY,
                        """{ name: ${cat.name}}"""
                    )
                }
            }
        }
        return 0
    }

}