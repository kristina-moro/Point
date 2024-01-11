package org.point.controller


import org.point.domain.Lang
import org.point.domain.db.DbMapper
import org.point.domain.db.Pageable
import org.point.domain.rest.AddUserRequest
import org.point.openApiUser.controller.api.UserApi
import org.point.openApiUser.controller.dto.AuthRequest
import org.point.openApiUser.controller.dto.AuthResponse
import org.point.openApiUser.controller.dto.EventLogRecord
import org.point.openApiUser.controller.dto.EventLogSortColumnEnum
import org.point.openApiUser.controller.dto.GetEventLogResponse
import org.point.openApiUser.controller.dto.SortDirection
import org.point.openApiUser.controller.dto.UpdatePasswordRequest
import org.point.service.EventLogService
import org.point.service.TokenService
import org.point.service.UserProfileService
import org.point.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.net.URI
import java.util.Locale
import java.util.UUID


@RestController
class UserController(
    private val tokenService: TokenService,
    private val service: UserService,
    private val profileService: UserProfileService,
    private val eventLogService: EventLogService
) : UserApi {
    val log = LoggerFactory.getLogger(javaClass)

    override fun addUser(
        request: Mono<org.point.openApiUser.controller.dto.AddUserRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        return request.doOnNext {
            log.info("Create a new user name=${it.name}, login=${it.login}")
        }.map {
            service.createUserRequest(
                AddUserRequest(it.name, it.login, it.password),
                getUserLanguage(exchange!!.request.headers.toSingleValueMap())
            )
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun confirmEmail(
        requestId: UUID,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        log.info("Requested email confirmation: requestId=$requestId")
        val userId = service.createUser(requestId)
        profileService.createProfile(userId)
        return Mono.just(
            ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:8080/webjars/swagger-ui/index.html")).build()
        )
    }

    override fun auth(request: Mono<AuthRequest>, exchange: ServerWebExchange?): Mono<ResponseEntity<AuthResponse>>? {
        return request.doOnNext {
            log.info("User ${it.login} logging in")
        }.map {
            service.checkPwd(it.login, it.password)
            ResponseEntity.status(HttpStatus.OK).body(AuthResponse().token("NEW-Token"))
        }
    }

    override fun setPassword(
        request: Mono<UpdatePasswordRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        return request.doOnNext {
            log.info("Requested set user password: userId=$userId")
        }.map {
            service.updatePassword(userId = userId, password = it.value)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun getEventLog(
        userId: UUID, // TODO: сделать необязательным и брать из токена
        page: Int?,
        limit: Int?,
        sortColumn: EventLogSortColumnEnum?,
        sortDirection: SortDirection?,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<GetEventLogResponse>>? {
        // TODO: parseToken(), проверить права доступа к данным пользователя
        log.info("Event log requested by user id=$(userId")
        val pageable = Pageable(
            page = page, limit = limit,
            sortBy = DbMapper.eventLogSortMapping[sortColumn ?: EventLogSortColumnEnum.datetime],
            sortDirection = sortDirection?.name
        )
        val items = eventLogService.getLog(userId, pageable)
            .map {
                EventLogRecord()
                    .id(it.id)
                    .eventType(it.eventType.name)
                    .datetime(it.datetime)
            }
        val total = eventLogService.getTotal(userId)

        return Mono.just(ResponseEntity.status(HttpStatus.OK)
            .body(GetEventLogResponse().items(items).total(total))
        )
    }

    /* ******************************************************** */
    private fun getUserLanguage(headers: Map<String, String>?): String? {
        return headers?.get(HttpHeaders.ACCEPT_LANGUAGE)?.let {
            Locale.LanguageRange.parse(it)[0].range.substring(0, 2)
        }
    }
}