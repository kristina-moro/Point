package org.point.exceptions

class ValidationException : CustomException {
    constructor(message: String) : super(code = Errors.VALIDATION_ERROR.name, message = message )

    /*constructor(error: Errors, parameter: Any? = null) : super(error = error, parameter)

    constructor(errors: List<String>) : super(
        ApiError(
            code = Errors.VALIDATION_ERROR.name,
            message = errors.joinToString(";"),
        )
    )*/
}