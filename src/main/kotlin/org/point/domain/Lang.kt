package org.point.domain

enum class Lang(val value: String) {
    EN("English"),
    RU("Русский"),
    FR("Français");

    companion object {
        fun valueOfOrNull(name: String): Lang? {
            return values().firstOrNull { it.name == name }
        }
    }
}

