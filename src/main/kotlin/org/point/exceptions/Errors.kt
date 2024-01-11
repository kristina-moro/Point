package org.point.exceptions

enum class Errors(val message: String) {
    INTERNAL_ERROR("Internal service error"),
    EXTERNAL_SERVICE_ERROR("External service call error: {0}"),
    ENTITY_NOT_FOUND("Entity {0} not found"),
    REQUEST_EXPIRED("Request expired"),
    ENTITY_NOT_UNIQUE("{0} already exists"),
    COULD_NOT_SAVE_ENTITY("Could not save entity"),
    CUSTOM_ERROR("Error occurred: {0}"),
    UNPROCESSABLE_ENTITY("Could not process entity"),
    BAD_REQUEST("Bad request parameters"),

    VALIDATION_ERROR("Validation failed: {0}"),
}
