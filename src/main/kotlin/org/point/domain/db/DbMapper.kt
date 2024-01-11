package org.point.domain.db

import org.point.openApiUser.controller.dto.EventLogSortColumnEnum

class DbMapper {
    companion object {
        val eventLogSortMapping: Map<EventLogSortColumnEnum, String> =
            mapOf(EventLogSortColumnEnum.datetime to "id")
    }
}