package org.point.domain

enum class EventType(val value: String) {
    USER_SIGNED_UP("Аккаунт создан"),
    EMAIL_CONFIRMED("Email confirmed"),

    USER_LOGGED_IN("Вход в систему"),
    USER_CHANGE_PASSWORD("Вход в систему"),

    NAME_UPDATED("Отредактировано имя"),
    LANGUAGE_UPDATED("Отредактирован язык"),
    DESCRIPTION_UPDATED("Отредактировано краткое описание"),
    ABOUT_UPDATED("Отредактировано описание"),
    ADDRESS_UPDATED("Отредактирован адрес"),
    GEO_UPDATED("Отредактированы gps координаты"),
    OPENING_HOURS_UPDATED("Отредактировано время работы"),
    VISIBILITY_UPDATED("Отредактирована видимость в поиске"),
    AVATAR_UPDATED("Обновлено изображение профиля"),
    AVATAR_DELETED("Удалено изображение профиля"),

    ADDED_CATEGORY("Добавлена категория исполнителя"),
    REMOVED_CATEGORY("Удалена категория исполнителя"),

    ADDED_FAVOURITES("Исполнитель добавлен в избранное"),
    REMOVED_FAVOURITES("Исполнитель удален из избранного"),

    PORTFOLIO_IMAGE_ADDED("Загружено изображение в портфолио"),
    PORTFOLIO_IMAGE_DELETED("Удалено изображение из портфолио"),
    PORTFOLIO_DESCRIPTION_UPDATED("Отредактировано описание и порядок сортировки изображений в портфолио"),
}
