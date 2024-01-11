package org.point.domain.rest

data class AddUserRequest(
    val name: String,
    val login: String,
    val password: String
)
