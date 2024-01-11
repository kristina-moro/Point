package org.point.service

import org.point.domain.rest.AddUserRequest
import org.point.exceptions.CustomException
import org.point.exceptions.Errors
import org.point.exceptions.ValidationException
import org.point.service.ValueValidationService.Companion.validateEmail
import org.point.service.ValueValidationService.Companion.validateLength
import org.point.service.ValueValidationService.Companion.validateMaxLength
import org.point.service.ValueValidationService.Companion.validateMinLength
import org.point.service.ValueValidationService.Companion.validateValueRequired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.InputStream
import java.net.URLConnection

@Service
class ValidationService {

    fun validate(request: AddUserRequest) {
        val errors = mutableListOf<String>()

        validateValueRequired(propertyName = "login", value = request.login, errors = errors)
        validateValueRequired(propertyName = "name", value = request.name, errors = errors)
        validateValueRequired(propertyName = "password", value = request.password, errors = errors)

        validateEmail(request.login, errors = errors)

        processErrors(errors)
    }

    fun validateName(name: String?, minLength: Int? = null, maxLength: Int? = null) {
        val errors = mutableListOf<String>()

        validateValueRequired(propertyName = "name", value = name, errors = errors)
        minLength?.let { validateMinLength(propertyName = "name", value = name!!, length = minLength, errors = errors) }
        maxLength?.let { validateMaxLength(propertyName = "name", value = name!!, length = maxLength, errors = errors) }

        processErrors(errors)
    }

    fun validateOpeningHours(value: String) {
        validateLength("Opening hours", value, 11)
        ValueValidationService.validateOpeningHours(value)
    }

    fun validateImage(bytes: InputStream, maxSizeKb: Int) {
        val contentType = URLConnection.guessContentTypeFromStream(bytes)
        if (!listOf(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE).contains(
                contentType
            )
        ) {
            throw CustomException(Errors.CUSTOM_ERROR, "Unknown mediatype")
        }
        if (bytes.readAllBytes().size > 1024 * maxSizeKb) {
            throw CustomException(Errors.CUSTOM_ERROR, "File size exceeds $maxSizeKb KBytes")
        }
    }

    private fun processErrors(errors: List<String>) {
        if (errors.isNotEmpty()) {
            throw ValidationException(errors.joinToString(separator = ", "))
        }
    }

}