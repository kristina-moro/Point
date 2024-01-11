package org.point.domain

/*
   Чамы работы исполнителя
*/
data class OpeningHours(
    val start: String, // format hh:mi
    val end: String,
) {
    constructor(value: String) : this(
        start = value.substringBefore('-'),
        end = value.substringAfter('-')
    )

    override fun toString(): String {
        return "$start-$end"
    }
}
