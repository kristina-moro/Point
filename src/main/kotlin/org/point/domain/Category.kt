package org.point.domain

/*
   Категории услуг
*/

data class Category(
    val id: Int,                     // идентификатор
    val name: String,                 // название
    val description: String? = null,  // подробное описание. Наверно не нужно, удалить
    val parentId: Int? = null,       // идентификатор родительской категории
)