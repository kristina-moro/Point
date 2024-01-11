package org.point.service

import org.point.config.ApplicationProperties
import org.point.domain.EventType
import org.point.domain.PortfolioImage
import org.point.exceptions.CustomException
import org.point.exceptions.Errors
import org.point.openApiUserPortfolio.controller.dto.ImageInformation
import org.point.repository.UserPortfolioRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.UUID

@Service
@Transactional
class UserPortfolioService(
    private val validationService: ValidationService,
    private val properties: ApplicationProperties,
    private val repository: UserPortfolioRepository,
    private val eventLogService: EventLogService
) {
    private val log = LoggerFactory.getLogger(javaClass)
    fun get(userId: UUID): List<PortfolioImage> {
        return repository.get(userId)
    }

    fun uploadImage(userId: UUID, description: String? = null, value: InputStream, sortOrder: Int? = null): Int {
        val image = value.readAllBytes()
        value.let { validationService.validateImage(bytes = ByteArrayInputStream(image), maxSizeKb = 2048) }
        // TODO: resize ?
        if (repository.getTotal(userId) >= properties.portfolioMaxSize) {
            throw CustomException("Максимально возможное количество изображений - ${properties.portfolioMaxSize}")
        }
        return repository.saveImage(userId, image).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.PORTFOLIO_IMAGE_ADDED)
            }
        }
    }

    fun deleteImage(userId: UUID, imageId: Int): Int {
        return repository.deleteImage(userId, imageId).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.PORTFOLIO_IMAGE_DELETED)
            }
        }
    }

    fun updateDescriptionAndOrder(userId: UUID, imageInformation: List<ImageInformation>): Int {
        return repository.update(userId, imageInformation).size.also {
            eventLogService.logEvent(userId = userId, eventType = EventType.PORTFOLIO_DESCRIPTION_UPDATED)
        }
    }

}