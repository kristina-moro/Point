package org.point.repository

import org.point.domain.User
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
class UserRepository(dataSource: DataSource) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = UserMapper()
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private val INSERT =
        "insert into users(id, name, login, password) values(:id, :name, :login, :password)"
    private val SELECT_BY_LOGIN = "select id, name, login, password, is_active from users where login = :login"
    private val SELECT_BY_ID = "select id, name, login, password, is_active from users where id = :id"
    private val UPDATE_NAME = "update users set name = :name where id = :id"
    private val UPDATE_PASSWORD = "update users set password = :password where id = :id"
    private val ACTIVATE_USER = "update users set is_active = true where id = :id"

    fun addUser(
        id: UUID,
        name: String,
        login: String,
        password: String
    ): User {
        jdbcTemplate.update(INSERT, mapOf("id" to id, "name" to name, "login" to login, "password" to password))
        return findById(id)!!
    }

    fun findByLogin(login: String): User? {
        return getOne(SELECT_BY_LOGIN, mapOf("login" to login))
    }

    fun findById(id: UUID): User? {
        return getOne(SELECT_BY_ID, mapOf("id" to id))
    }

    fun activateUser(id: UUID) {
        jdbcTemplate.update(ACTIVATE_USER, mapOf("id" to id))
    }

    fun updateUser(
        id: UUID,
        name: String? = null,
        password: String? = null
    ): Int {

        name?.let {
            jdbcTemplate.update(UPDATE_NAME, mapOf("name" to name, "id" to id))
        }

        password?.let {
            jdbcTemplate.update(UPDATE_PASSWORD, mapOf("password" to password, "id" to id))
        }

        return 1
    }

    /* ********************************************** */

    private fun getOne(select: String, params: Map<String, Any>): User? {
        val sqlParams = MapSqlParameterSource()
        params.forEach { (k, v) -> sqlParams.addValue(k, v) }
        return try {
            jdbcTemplate.queryForObject(select, params, mapper)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }


    @Component
    private class UserMapper : RowMapper<User> {
        override fun mapRow(rs: ResultSet, rowNum: Int): User {
            return User(
                id = UUID.fromString(rs.getString("id")),
                name = rs.getString("name"),
                login = rs.getString("login"),
                password = rs.getString("password"),
                isActive = rs.getBoolean("is_active"),
            )
        }
    }
}