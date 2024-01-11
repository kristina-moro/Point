package org.point.exceptions

data class ApiError(
    val code: String,
    val message: String,
)