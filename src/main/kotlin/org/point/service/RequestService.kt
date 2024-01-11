package org.point.service

import org.point.domain.Request
import org.point.repository.RequestRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class RequestService(private val requestRepository: RequestRepository) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun createUserRequest(userId: UUID, userName: String, login: String, password: String): UUID {
        val requestId = requestRepository.save(
            type = "EMAIL_CONFIRMATION",
            userId = userId,
            userName = userName,
            login = login,
            password = password,
            validTo = OffsetDateTime.now().plusDays(1)
        )
        return requestId
    }

    // оформить типы реквестов в enum ?
    fun get(id: UUID, type: String): Request? {
        val request = requestRepository.find(id = id)

        if (request == null || request.type != type) {
            log.info("Request id=$id, type=$type not found")
            return null
        }

        if (request.validTo < OffsetDateTime.now()) {
            log.info("Request id=$id expired")
            return null
        }

        return request
    }
}