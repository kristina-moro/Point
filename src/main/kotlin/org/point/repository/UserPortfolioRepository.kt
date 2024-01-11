package org.point.repository

import org.point.domain.PortfolioImage
import org.point.openApiUserPortfolio.controller.dto.ImageInformation
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID
import javax.sql.DataSource


@Repository
class UserPortfolioRepository(dataSource: DataSource) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = PortfolioMapper()
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val simpleJdbcTemplate: org.springframework.jdbc.core.JdbcTemplate = org.springframework.jdbc.core.JdbcTemplate(dataSource)

    private val SELECT = """SELECT user_id, id, description, sort_order as sort_order, image 
                            FROM user_portfolio
                            WHERE user_id = :user_id
                            ORDER BY sort_order, id""".trimMargin()
    private val SELECT_TOTAL = "SELECT count(*) total FROM user_portfolio WHERE user_id = :user_id"

    private val INSERT = "INSERT INTO user_portfolio(user_id, image) values(:user_id, :image)"
    private val DELETE = "DELETE FROM user_portfolio WHERE user_id = :user_id and id = :id"
    private val UPDATE = """UPDATE user_portfolio 
                            SET description = :description, 
                                sort_order = :sort_order 
                            WHERE user_id = :user_id and id = :id""".trimMargin()

    fun get(userId: UUID): List<PortfolioImage> {
        return jdbcTemplate.query(
            SELECT,
            mapOf("user_id" to userId),
            mapper
        )
    }

    fun getTotal(userId: UUID): Int {
        return jdbcTemplate.queryForObject(
            SELECT_TOTAL,
            mapOf("user_id" to userId),
            Int::class.java
        ) ?: 0
    }

    fun saveImage(userId: UUID, value: ByteArray?): Int {
        return SimpleJdbcInsert(simpleJdbcTemplate)
           .withTableName("user_portfolio")
           .usingGeneratedKeyColumns("id")
           .executeAndReturnKey(mapOf("user_id" to userId, "image" to value)).toInt()
    }

    fun deleteImage(userId: UUID, id: Int): Int {
        return jdbcTemplate.update(DELETE, mapOf("user_id" to userId, "id" to id))
    }

    fun update(userId: UUID, infos: List<ImageInformation>): IntArray {
        val params = infos.sortedBy { it.sortOrder }
            .mapIndexed { index, info ->
                mapOf(
                    "user_id" to userId,
                    "id" to info.id,
                    "description" to info.description,
                    "sort_order" to index + 1
                )
            }
            .toTypedArray()
        return jdbcTemplate.batchUpdate(UPDATE, params)
    }

    /* ************************************** */

    @Component
    private class PortfolioMapper : RowMapper<PortfolioImage> {
        override fun mapRow(rs: ResultSet, rowNum: Int): PortfolioImage {
            return PortfolioImage(
                userId = UUID.fromString(rs.getString("user_id")),
                id = rs.getInt("id"),
                description = rs.getString("description"),
                sortOrder = rs.getInt("sort_order"),
                data = rs.getBytes("image"),
            )
        }
    }
}


