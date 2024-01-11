package org.point.exceptions

import java.text.MessageFormat

open class CustomException(val error: ApiError) : RuntimeException(error.message) {
    constructor(error: Errors, vararg parameters: Any?) : this(
        ApiError(
            code = error.name,
            message = MessageFormat.format(error.message, *parameters.map { it.toString() }.toTypedArray())
        )
    )

    constructor(code: String, message: String) : this(
        ApiError(code, message)
    )

    constructor(message: String) : this(
        ApiError(Errors.CUSTOM_ERROR.name, message)
    )
}