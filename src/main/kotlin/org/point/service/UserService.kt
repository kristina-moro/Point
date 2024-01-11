package org.point.service

import org.point.domain.EventType
import org.point.domain.Lang
import org.point.domain.User
import org.point.domain.rest.AddUserRequest
import org.point.exceptions.CustomException
import org.point.exceptions.Errors
import org.point.exceptions.NotFoundException
import org.point.exceptions.NotUniqueException
import org.point.repository.UserProfileRepository
import org.point.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import java.security.spec.KeySpec
import java.util.HexFormat
import java.util.UUID
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Service
@Transactional
class UserService(
    private val validationService: ValidationService,
    private val repository: UserRepository,
    private val profileRepository: UserProfileRepository,
    private val requestService: RequestService,
    private val mailer: Mailer,
    private val eventLogService: EventLogService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val ALGORITHM = "PBKDF2WithHmacSHA512"
    private val ITERATIONS = 120_000
    private val KEY_LENGTH = 256
    private val secret = "top"

    fun getUser(id: UUID) = repository.findById(id) ?: throw NotFoundException("User id=$id not found")

    fun createUserRequest(request: AddUserRequest, lang: String?): UUID {
        validationService.validate(request)
        val language = lang?.let { Lang.valueOfOrNull(lang)}  ?: Lang.EN

        repository.findByLogin(request.login)?.let {
            throw NotUniqueException("User with email ${request.login}")
        }

        val userId = UUID.randomUUID()
        val requestId = requestService.createUserRequest(
            userId = userId,
            userName = request.name,
            login = request.login,
            password = generateHash(request.password, userId.toString())
        )
        val uri = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(8080)
            .pathSegment("user")
            .pathSegment("confirm-email")
            .queryParam("request_id", requestId)
            .build()

        mailer.sendEmailConfirmationEmail(
            userName = request.name,
            login = request.login,
            lang = language.name,
            uri = uri.toUriString()
        )
        // mailer.sendWelcomeEmail(user)

        return requestId
    }

    fun createUser(requestId: UUID): UUID {
        val request = requestService.get(id = requestId, type = "EMAIL_CONFIRMATION")
            ?: throw NotFoundException(message = "Invalid request") // todo: bad request

        repository.findByLogin(request.login)?.let {
            log.info("User login=${request.login} exists and will not be created")
            return it.id
        }

        // user
        val user = repository.addUser(
            id = UUID.randomUUID(),
            name = request.userName,
            login = request.login,
            password = request.password
        )
        eventLogService.logEvent(
            userId = user.id,
            eventType = EventType.USER_SIGNED_UP,
            note = """{ login: ${request.login}, name: ${request.userName}}""",
            datetime = request.validFrom
        )

        // profile
        profileRepository.create(user.id)
        eventLogService.logEvent(userId = user.id, eventType = EventType.EMAIL_CONFIRMED, note = user.login)
        return user.id
    }

    fun updatePassword(
        userId: UUID,
        password: String?
    ): Int {
        eventLogService.logEvent(userId = userId, eventType = EventType.USER_CHANGE_PASSWORD)
        ValueValidationService.validateValueRequired(propertyName = "password", value = password)
        return repository.updateUser(
            id = userId,
            password = generateHash(password!!, userId.toString())
        )
    }

    fun checkPwd(login: String, password: String) {
        val user = findByLogin(login)
        if (user.password != generateHash(password, user.id.toString())) {
            throw CustomException(Errors.CUSTOM_ERROR, "Login or password is wrong")
        }
        eventLogService.logEvent(userId = user.id, eventType = EventType.USER_LOGGED_IN)
    }

    /* ************************************************************* */

    private fun findByLogin(login: String): User {
        return repository.findByLogin(login) ?: throw NotFoundException("User $login not found")
    }

    private fun generateHash(password: String, salt: String): String {
        val combinedSalt = "$salt$secret".toByteArray()
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
        val key: SecretKey = factory.generateSecret(spec)
        val hash: ByteArray = key.encoded
        return hash.toHexString()
    }

    private fun ByteArray.toHexString(): String =
        HexFormat.of().formatHex(this)
}