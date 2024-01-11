package org.point.exceptions

class NotUniqueException(message: String) : CustomException(Errors.ENTITY_NOT_UNIQUE, message)
