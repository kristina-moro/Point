package org.point.repository

import org.point.domain.Lang
import org.point.domain.OpeningHours
import org.point.domain.UserProfile
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
class UserProfileRepository(dataSource: DataSource) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = ProfileMapper()
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private val SELECT = """SELECT p.user_id, u.name, u.login, p.lang, 
                                   p.phone_number, p.email, p.avatar, 
                                   p.description, p.about, p.address, p.geo_latitude, p.geo_longitude,
                                   p.opening_hours, p.is_active, p.is_hidden 
                            FROM users u 
                              JOIN user_profile p on p.user_id = u.id
                            WHERE u.id = :user_id""".trimMargin()
    private val INSERT = "INSERT INTO user_profile(user_id, lang, email) values(:user_id, :lang, :email)"

    private val UPDATE_LANG = "UPDATE user_profile SET lang = :lang WHERE user_id = :user_id"
    private val UPDATE_AVATAR = "UPDATE user_profile SET avatar = :avatar WHERE user_id = :user_id"
    private val DELETE_AVATAR = "UPDATE user_profile SET avatar = null WHERE user_id = :user_id"
    private val UPDATE_DESC = "UPDATE user_profile SET description = :description WHERE user_id = :user_id"
    private val UPDATE_ABOUT = "UPDATE user_profile SET about = :about WHERE user_id = :user_id"
    private val UPDATE_ADDR = "UPDATE user_profile SET address = :address WHERE user_id = :user_id"
    private val UPDATE_GEO =
        "UPDATE user_profile SET geo_latitude = :geo_latitude, geo_longitude = :geo_longitude WHERE user_id = :user_id"
    private val UPDATE_HOURS = "UPDATE user_profile SET opening_hours = :value WHERE user_id = :user_id"
    private val UPDATE_IS_HIDDEN = "UPDATE user_profile SET is_hidden = :is_hidden WHERE user_id = :user_id"

    fun create(userId: UUID): Int {
        if (get(userId) != null) {
            return 1
        }
        return jdbcTemplate.update(INSERT, mapOf("user_id" to userId, "lang" to "EN", "email" to "some@gmail.com"))
    }

    fun get(userId: UUID): UserProfile? {
        return getOne(SELECT, mapOf("user_id" to userId))
    }

    fun updateLang(userId: UUID, value: String): Int {
        return jdbcTemplate.update(UPDATE_LANG, mapOf("user_id" to userId, "lang" to value))
    }

    fun updateAvatar(userId: UUID, value: ByteArray?): Int {
        return jdbcTemplate.update(UPDATE_AVATAR, mapOf("user_id" to userId, "avatar" to value))
    }

    fun deleteAvatar(userId: UUID): Int {
        return jdbcTemplate.update(DELETE_AVATAR, mapOf("user_id" to userId))
    }

    fun updateDescription(userId: UUID, value: String?): Int {
        return jdbcTemplate.update(UPDATE_DESC, mapOf("user_id" to userId, "description" to value))
    }

    fun updateAbout(userId: UUID, value: String?): Int {
        return jdbcTemplate.update(UPDATE_ABOUT, mapOf("user_id" to userId, "about" to value))
    }

    fun updateAddress(userId: UUID, value: String?): Int {
        return jdbcTemplate.update(UPDATE_ADDR, mapOf("user_id" to userId, "address" to value))
    }

    fun updateGPS(userId: UUID, geoLatitude: Double?, geoLongitude: Double?): Int {
        return jdbcTemplate.update(
            UPDATE_GEO,
            mapOf("user_id" to userId, "geo_latitude" to geoLatitude, "geo_longitude" to geoLongitude)
        )
    }

    fun updateHours(userId: UUID, value: String?): Int {
        return jdbcTemplate.update(UPDATE_HOURS, mapOf("user_id" to userId, "value" to value))
    }

    fun updateHidden(userId: UUID, value: Boolean): Int {
        return jdbcTemplate.update(UPDATE_IS_HIDDEN, mapOf("user_id" to userId, "value" to value))
    }

    /* ************************************** */

    private fun getOne(select: String, params: Map<String, Any>): UserProfile? {
        val sqlParams = MapSqlParameterSource()
        params.forEach { (k, v) -> sqlParams.addValue(k, v) }
        return try {
            jdbcTemplate.queryForObject(select, params, mapper)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    @Component
    private class ProfileMapper : RowMapper<UserProfile> {
        override fun mapRow(rs: ResultSet, rowNum: Int): UserProfile {
            return UserProfile(
                userId = UUID.fromString(rs.getString("user_id")),
                name = rs.getString("name"),
                login = rs.getString("login"),
                lang = Lang.valueOf(rs.getString("lang")),
                phoneNumber = rs.getString("phone_number"),
                email = rs.getString("email"),
                avatar = rs.getBytes("avatar"),
                description = rs.getString("description"),
                about = rs.getString("about"),
                address = rs.getString("address"),
                geoLatitude = rs.getDouble("geo_latitude"),
                geoLongitude = rs.getDouble("geo_longitude"),
                openingHours = rs.getString("opening_hours")?.let { OpeningHours(it) },
                isActive = rs.getBoolean("is_active"),
                isHidden = rs.getBoolean("is_hidden"),
            )
        }
    }
}


