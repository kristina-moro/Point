package org.point.exceptions

class NotFoundException(message: String) : CustomException(code = Errors.ENTITY_NOT_FOUND.name, message = message)