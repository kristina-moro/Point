package org.point.service

import org.point.exceptions.Errors
import org.point.exceptions.ValidationException
import java.math.BigDecimal
import java.text.MessageFormat
import java.time.OffsetDateTime

class ValueValidationService {
    companion object {

        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        private val HOURS_REGEX = Regex("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]-(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")

        //private val COULD_NOT_PARSE_REQUEST_PARAMETERS = "Could not parse request parameters"
        private val INVALID_PARAMETER_VALUE = "Invalid parameter value: {0}"
        private val REQUIRED_FIELD_MISSING = "Required field {0} is missing"
        private val SHOULD_BE_POSITIVE = "{0} value should be positive"
        private val SHOULD_BE_ZERO_OR_POSITIVE = "{0} value should be zero or positive"
        private val DATE_SHOULD_BE_IN_FUTURE = "{0} date should be in future"
        private val WRONG_DECIMAL_FORMAT = "{0} wrong decimal number format"

        fun validateValueRequired(propertyName: String, value: Any?) {
            if (value == null || (value is String && value.isBlank())) {
                throw ValidationException(MessageFormat.format(REQUIRED_FIELD_MISSING, propertyName))
            }
        }

        fun validateValueRequired(propertyName: String, value: Any?, errors: MutableList<String>) {
            if (value == null || (value is String && value.isBlank())) {
                errors.add(MessageFormat.format(REQUIRED_FIELD_MISSING, propertyName))
            }
        }

        fun validateZeroOrPositive(propertyName: String, value: Int?, errors: MutableList<String>) {
            if (value != null && value < 0) {
                errors.add(MessageFormat.format(SHOULD_BE_ZERO_OR_POSITIVE, propertyName))
            }
        }

        fun validatePositive(propertyName: String, value: Int?) {
            if (value != null && value <= 0) {
                throw ValidationException(MessageFormat.format(SHOULD_BE_POSITIVE, propertyName))
            }
        }

        fun validatePositive(propertyName: String, value: Int?, errors: MutableList<String>) {
            if (value != null && value <= 0) {
                errors.add(MessageFormat.format(SHOULD_BE_POSITIVE, propertyName))
            }
        }

        fun validatePositive(propertyName: String, value: BigDecimal?, errors: MutableList<String>) {
            if (value != null && value <= BigDecimal.ZERO) {
                errors.add(MessageFormat.format(SHOULD_BE_POSITIVE, propertyName))
            }
        }

        fun validateDateIsInFuture(propertyName: String, value: OffsetDateTime?, errors: MutableList<String>) {
            if (value != null && value.isBefore(OffsetDateTime.now())) {
                errors.add(MessageFormat.format(DATE_SHOULD_BE_IN_FUTURE, propertyName))
            }
        }

        fun validateEmail(email: String, errors: MutableList<String>) {
            if (!EMAIL_REGEX.matches(email)) {
                errors.add(MessageFormat.format(INVALID_PARAMETER_VALUE, email))
            }
        }

        fun validateValueBigDecimal(propertyName: String, value: String?, errors: MutableList<String>) {
            if (value != null && value.toBigDecimalOrNull() == null) {
                errors.add(MessageFormat.format(WRONG_DECIMAL_FORMAT, propertyName))
            }
        }

        fun validateValueInteger(propertyName: String, value: String?, errors: MutableList<String>) {
            if (value != null && value.toIntOrNull() == null) {
                errors.add("$propertyName should be a valid number")
            }
        }

        fun validateLength(propertyName: String, value: String, length: Int) {
            if (value.length != length) {
                throw ValidationException("$propertyName should contain exactly $length symbols")
            }
        }

        fun validateLength(propertyName: String, value: String, length: Int, errors: MutableList<String>) {
            if (value.length != length) {
                errors.add("$propertyName should contain exactly $length symbols")
            }
        }

        fun validateMaxLength(propertyName: String, value: String, length: Int, errors: MutableList<String>) {
            if (value.length > length) {
                errors.add("$propertyName should contain less than or $length symbols")
            }
        }

        fun validateMinLength(propertyName: String, value: String, length: Int, errors: MutableList<String>) {
            if (value.length < length) {
                errors.add("$propertyName should contain more than or $length symbols")
            }
        }

        fun validateMonth(propertyName: String, value: String, errors: MutableList<String>) {
            val month = value.toIntOrNull()
            if (month != null && (month < 1 || month > 12)) {
                errors.add("$propertyName: the month should be between 01 and 12")
            }
        }

        fun validateOpeningHours(value: String) {
            if (!HOURS_REGEX.matches(value)) {
                throw ValidationException(MessageFormat.format(INVALID_PARAMETER_VALUE, value))
            }
            // todo: begin < end
        }
    }
}
