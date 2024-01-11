package org.point.service

import org.point.domain.EventType
import org.point.domain.Lang
import org.point.domain.UserProfile
import org.point.exceptions.NotFoundException
import org.point.repository.UserProfileRepository
import org.point.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.util.UUID

@Service
@Transactional
class UserProfileService(
    private val validationService: ValidationService,
    private val repository: UserRepository,
    private val profileRepository: UserProfileRepository,
    private val eventLogService: EventLogService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getUser(id: UUID) = repository.findById(id) ?: throw NotFoundException("User id=$id not found")

    fun updateName(
        userId: UUID,
        name: String?,
    ): Int {
        validationService.validateName(name, 2, 20)
        return repository.updateUser(id = userId, name = name).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.NAME_UPDATED, note = name)
            }
        }
    }

    fun createProfile(userId: UUID): Int {
        return profileRepository.create(userId)
    }

    fun getProfile(userId: UUID): UserProfile {
        return profileRepository.get(userId) ?: throw NotFoundException("Profile for user id=$userId not found")
    }

    fun updateProfile(userId: UUID, properties: Map<String, String?>): Int {
        val errors = mutableListOf<String>()
        properties.forEach { (propertyName, propertyValue) ->
            when (propertyName) {
                "lang" -> updateLang(userId, propertyValue) // TODO: to enum
                /*
                "phoneNumber" -> print("x == 1")
                "description" -> print("x == 1")
                "about" -> print("x == 1")
                "address" -> print("x == 1")
                "gps" -> print("x == 1")
                "openingHours" -> print("x == 1")
                "isHidden" -> print("x == 1")*/
                else -> { // Note the block
                    errors.add("Property $propertyName not found")
                }
            }
        }
        return 1
    }

    fun updateLang(userId: UUID, value: String?): Int {
        ValueValidationService.validateValueRequired("Language", value)
        val lang = Lang.valueOfOrNull(value!!.trimIndent()) ?: throw NotFoundException("Language $value is not supported")
        return profileRepository.updateLang(userId, lang.name).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.LANGUAGE_UPDATED, note = lang.name)
            }
        }
    }

    fun updateAvatar(userId: UUID, value: InputStream?): Int {
        value?.let { validationService.validateImage(bytes = value, maxSizeKb = 2048) }
        // TODO: resize
        return profileRepository.updateAvatar(userId, value?.readAllBytes()).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.AVATAR_UPDATED)
            }
        }
    }

    fun deleteAvatar(userId: UUID): Int {
        return profileRepository.deleteAvatar(userId).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.AVATAR_DELETED)
            }
        }
    }

    fun updateDescription(userId: UUID, value: String?): Int {
        return profileRepository.updateDescription(userId, value).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.DESCRIPTION_UPDATED, note = value)
            }
        }
    }

    fun updateAbout(userId: UUID, value: String?): Int {
        return profileRepository.updateAbout(userId, value).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.ABOUT_UPDATED, note = value)
            }
        }
    }

    fun updateAddress(userId: UUID, value: String?): Int {
        return profileRepository.updateAddress(userId, value).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.ADDRESS_UPDATED, note = value)
            }
        }
    }

    fun updateGPS(userId: UUID, value: String?): Int {
        val geoLatitude = value?.substringBefore(',')?.trim()?.toDouble()
        val geoLongitude = value?.substringAfter(',')?.trim()?.toDouble()
        // todo: validate it

        return profileRepository.updateGPS(userId, geoLatitude, geoLongitude).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.GEO_UPDATED, note = value)
            }
        }
    }

    fun updateOpeningHours(userId: UUID, value: String?): Int {
        value?.let { validationService.validateOpeningHours(value) }
        return profileRepository.updateHours(userId, value).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.OPENING_HOURS_UPDATED, note = value)
            }
        }
    }

    fun setProfileHidden(userId: UUID, value: Boolean): Int {
        return profileRepository.updateHidden(userId, value).also {
            if (it == 1) {
                eventLogService.logEvent(userId = userId, eventType = EventType.VISIBILITY_UPDATED, note = value.toString())
            }
        }
    }

}