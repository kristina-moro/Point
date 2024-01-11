package org.point.domain

import java.util.UUID

data class User(
    val id: UUID,            // идентификатор
    val name: String,        // имя для использования в интерфейсе
    val login: String,       // логин
    val password: String,    // хэш пароля
    val isActive: Boolean    // статус пользователя
)
