package org.point.exceptions

class InternalServerErrorException(message: String) : CustomException(message, Errors.INTERNAL_ERROR.name)