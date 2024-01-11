package org.point.repository

import org.point.domain.Request
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
class RequestRepository(dataSource: DataSource) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = RequestMapper()
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private val INSERT = "insert into request(id, type, user_id, user_name, login, password, valid_from, valid_to)" +
            " values(:id, :type, :user_id, :user_name, :login, :password, :valid_from, :valid_to)"
    private val SELECT = "select id, type, user_id, user_name, login, password, valid_from, valid_to from request where id = :id"
    private val DELETE_EXPIRED = "delete from request where valid_to < NOW()"

    private val emptyMap = HashMap<String, Any>()

    fun save(
        type: String,
        userId: UUID,
        userName: String,
        login: String,
        password: String,
        validTo: OffsetDateTime? = null
    ): UUID {
        val id = UUID.randomUUID()
        val now = OffsetDateTime.now()
        jdbcTemplate.update(
            INSERT, mapOf(
                "id" to id,
                "type" to type,
                "user_id" to userId,
                "user_name" to userName,
                "login" to login,
                "password" to password,
                "valid_from" to now,
                "valid_to" to (validTo ?: now.plusDays(1))
            )
        )
        return id
    }

    fun find(id: UUID): Request? {
        val params = MapSqlParameterSource()
        params.addValue("id", id)
        return jdbcTemplate.query(SELECT, params, mapper).firstOrNull()
    }

    fun deleteExpiredRequests() {
        jdbcTemplate.update(DELETE_EXPIRED, emptyMap)
    }


    @Component
    private class RequestMapper : RowMapper<Request> {
        override fun mapRow(rs: ResultSet, rowNum: Int): Request {
            return Request(
                id = UUID.fromString(rs.getString("id")),
                type = rs.getString("type"),
                userId = UUID.fromString(rs.getString("user_id")),
                userName = rs.getString("user_name"),
                login = rs.getString("login"),
                password = rs.getString("password"),
                validFrom = rs.getTimestamp("valid_from").toLocalDateTime().atOffset(ZoneOffset.UTC),
                validTo = rs.getTimestamp("valid_to").toLocalDateTime().atOffset(ZoneOffset.UTC)
            )
        }
    }
}