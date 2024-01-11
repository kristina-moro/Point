package org.point.repository

import org.point.domain.Category
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID
import javax.sql.DataSource


@Repository
class CategoryRepository(dataSource: DataSource) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = CategoryMapper()
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private val SELECT = "select id, name, description, parent_id from category"
    private val SELECT_BY_ID = "$SELECT where id = :id"
    private val SELECT_BY_PARENT_ID = "$SELECT where parent_id = :parent_id"

    private val SELECT_USER_CATEGORY =
        "SELECT c.id, c.name, c.description, c.parent_id FROM user_category uc JOIN category c ON c.id = uc.category_id WHERE uc.user_id = :user_id"
    private val ADD_USER_CATEGORY = "INSERT INTO user_category(user_id, category_id) values(:user_id, :category_id)"
    private val REMOVE_USER_CATEGORY =
        "DELETE FROM user_category WHERE user_id = :user_id and category_id = :category_id"

    fun getCategory(categoryId: Int): Category? {
        val params = MapSqlParameterSource()
        params.addValue("id", categoryId)
        return jdbcTemplate.queryForObject(
            SELECT_BY_ID,
            params,
            mapper
        )
    }

    fun getCatalogByParentId(categoryId: Int?): List<Category> {
        val params = MapSqlParameterSource()
        params.addValue("parent_id", categoryId)
        return jdbcTemplate.query(
            if (categoryId != null) SELECT_BY_PARENT_ID else SELECT,
            params,
            mapper
        )
    }

    /* ****  USER CAT **** */
    fun getUserCategory(userId: UUID): List<Category> {
        val params = MapSqlParameterSource()
        params.addValue("user_id", userId)
        return jdbcTemplate.query(
            SELECT_USER_CATEGORY,
            params,
            mapper
        )
    }

    fun addUserCategory(userId: UUID, categoryId: Int): Int {
        return jdbcTemplate.update(ADD_USER_CATEGORY, mapOf("user_id" to userId, "category_id" to categoryId))
    }

    fun removeUserCategory(userId: UUID, categoryId: Int): Int {
        return jdbcTemplate.update(REMOVE_USER_CATEGORY, mapOf("user_id" to userId, "category_id" to categoryId))
    }

    /* ************************************** */

    @Component
    private class CategoryMapper : RowMapper<Category> {
        override fun mapRow(rs: ResultSet, rowNum: Int): Category {
            return Category(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                parentId = rs.getInt("parent_id")
            )
        }
    }
}


