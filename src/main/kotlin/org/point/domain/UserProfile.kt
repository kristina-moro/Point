package org.point.domain

import java.awt.geom.Point2D
import java.util.UUID

data class UserProfile(
    val userId: UUID,                         // идентификатор пользователя
    val name: String,                         // имя для использования в интерфейсе
    val login: String,                        // логин
    val email: String? = null,                // зачем тут email, если он есть в users?
    val lang: Lang? = null,                   // код языка пользователя
    val phoneNumber: String? = null,
    val avatar: ByteArray? = null,            // img
    val description: String? = null,          // краткое описание пользователя

    // для исполнителей
    val about: String? = null,                // расширенное описание пользователя
    val address: String? = null,              // для показа на фронте, если исполнитель хочет показывать свой адрес
    val geoLatitude: Double? = null,          // преднастроенная локация для поиска предложений данного исполнителя
    val geoLongitude: Double? = null,
    val openingHours: OpeningHours? = null,   // часы работы

    val isActive: Boolean,                   // активный или заблокированный профиль. Почему бы не перенести на уровено пользователя в таблицу USERS ?
    val isHidden: Boolean,                   // показывать в поиске
)
