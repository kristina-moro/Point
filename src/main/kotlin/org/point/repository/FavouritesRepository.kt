package org.point.repository

import org.point.domain.Favourites
import org.point.domain.User
import org.point.service.PaginationUtils.Companion.getSql
import org.slf4j.LoggerFactory
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID
import javax.sql.DataSource

@Repository
class FavouritesRepository(dataSource: DataSource) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = FavouritesMapper()
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private val INSERT =
        "insert into favourites(user_id, performer_id, name) values(:user_id, :performer_id, :name)"
    private val DELETE =
        "delete from favourites where user_id = :user_id and performer_id = :performer_id"
    private val UPDATE =
        "update favourites set name = :name where user_id = :user_id and performer_id = :performer_id"

    private val SELECT_BY_USER_ID = "select id, user_id, performer_id, name from favourites where user_id = :user_id"


    fun add(
        userId: UUID,
        performerId: UUID,
        name: String? = null
    ): Int {
        try {
            return jdbcTemplate.update(
                INSERT,
                mapOf("user_id" to userId, "performer_id" to performerId, "name" to name)
            )
        } catch (ex: org.springframework.dao.DuplicateKeyException) {
            log.info(ex.message)
            return 0
        }
    }

    fun delete(
        userId: UUID,
        performerId: UUID
    ): Int {
        return jdbcTemplate.update(DELETE, mapOf("user_id" to userId, "performer_id" to performerId))
    }

    fun updateName(
        userId: UUID,
        performerId: UUID,
        name: String?
    ): Int {
        return jdbcTemplate.update(UPDATE, mapOf("user_id" to userId, "performer_id" to performerId, "name" to name))
    }

    fun findByUser(userId: UUID): List<Favourites> {
        val params = MapSqlParameterSource()
        params.addValue("user_id", userId)

        return jdbcTemplate.query(
            SELECT_BY_USER_ID,
            params,
            mapper
        )
    }

    /* ********************************************** */

    @Component
    private class FavouritesMapper : RowMapper<Favourites> {
        override fun mapRow(rs: ResultSet, rowNum: Int): Favourites {
            return Favourites(
                id = rs.getInt("id"),
                userId = UUID.fromString(rs.getString("user_id")),
                performerId = UUID.fromString(rs.getString("performer_id")),
                name = rs.getString("name"),
            )
        }
    }
}