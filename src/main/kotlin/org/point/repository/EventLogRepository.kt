package org.point.repository

import org.point.domain.EventLogItem
import org.point.domain.EventType
import org.point.domain.db.Pageable
import org.point.service.PaginationUtils.Companion.getSql
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.sql.DataSource

@Repository
class EventLogRepository(dataSource: DataSource) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = EventLogMapper()
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private val INSERT = "insert into event_log(user_id, event_type, note) values(:userId, :eventType, :note)"
    private val INSERT_DT = "insert into event_log(user_id, event_type, note, datetime) values(:userId, :eventType, :note, :datetime)"
    private val SELECT_BY_USER_ID =
        "SELECT id, user_id, event_type, note, datetime FROM event_log WHERE user_id = :user_id"
    private val SELECT_TOTAL_BY_USER_ID = "select count(*) total from ($SELECT_BY_USER_ID)"

    fun logEvent(
        userId: UUID,
        eventType: String,
        note: String?,
        datetime: OffsetDateTime? = null
    ): Int {
        return if (datetime == null) {
            jdbcTemplate.update(INSERT, mapOf("userId" to userId, "eventType" to eventType, "note" to note))
        }
        else {
            jdbcTemplate.update(
                INSERT_DT,
                mapOf(
                    "userId" to userId,
                    "eventType" to eventType,
                    "note" to note,
                    "datetime" to datetime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
                )
            )
        }
    }

    fun findTotalByUserId(userId: UUID): Int {
        val params = MapSqlParameterSource()
        params.addValue("user_id", userId)

        return jdbcTemplate.queryForObject(
            SELECT_TOTAL_BY_USER_ID,
            params,
            Int::class.java
        ) ?: 0
    }

    fun findByUserId(userId: UUID, pageable: Pageable): List<EventLogItem> {
        val params = MapSqlParameterSource()
        params.addValue("user_id", userId)

        return jdbcTemplate.query(
            SELECT_BY_USER_ID + pageable.getSql(),
            params,
            mapper
        )
    }


    @Component
    private class EventLogMapper : RowMapper<EventLogItem> {
        override fun mapRow(rs: ResultSet, rowNum: Int): EventLogItem {
            return EventLogItem(
                id = checkNotNull(rs.getInt("id")),
                userId = UUID.fromString(rs.getString("user_id")),
                eventType = EventType.valueOf(rs.getString("event_type")),
                note = rs.getString("note"),
                datetime = rs.getTimestamp("datetime").toLocalDateTime().atOffset(ZoneOffset.UTC)
            )
        }
    }
}