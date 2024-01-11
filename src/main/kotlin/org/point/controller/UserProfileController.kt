package org.point.controller

import org.point.openApiUserProfile.controller.api.UserProfileApi
import org.point.openApiUserProfile.controller.dto.GetProfileResponse
import org.point.openApiUserProfile.controller.dto.LanguageEnum
import org.point.openApiUserProfile.controller.dto.UpdatePropertyRequest
import org.point.service.TokenService
import org.point.service.UserProfileService
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID


@RestController
class UserProfileController(
    private val tokenService: TokenService,
    private val service: UserProfileService,
) : UserProfileApi {
    val log = LoggerFactory.getLogger(javaClass)

    override fun getProfile(userId: UUID, exchange: ServerWebExchange?): Mono<ResponseEntity<GetProfileResponse>> {
        val profile = service.getProfile(userId)
        val res = GetProfileResponse().id(profile.userId)
            .name(profile.name)
            .lang(profile.lang?.name)
            .phoneNumber(profile.phoneNumber)
            .email(profile.email)
            .avatar(profile.avatar)
            .description(profile.description)
            .about(profile.about)
            .address(profile.address)
            .gps("${profile.geoLatitude}, ${profile.geoLatitude}")
            .openingHours(profile.openingHours.toString())
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(res))
    }

    override fun setName(
        request: Mono<UpdatePropertyRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>>? {
        val userId = tokenService.parseToken()
        return request.doOnNext {
            log.info("User id=$userId requested change name")
        }.map {
            service.updateName(userId = userId, name = it.value)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun setLanguage(lang: LanguageEnum, exchange: ServerWebExchange?): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        log.info("Requested update language ${lang.name} for user id=$userId")
        service.updateLang(userId = userId, value = lang.name)
        return Mono.just(ResponseEntity.status(HttpStatus.OK).build())
    }

    override fun setDescription(
        request: Mono<UpdatePropertyRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        return request.doOnNext {
            log.info("Requested update description for user id=$userId")
        }.map {
            service.updateDescription(userId = userId, value = it.value)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun setAbout(
        request: Mono<UpdatePropertyRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        return request.doOnNext {
            log.info("Requested update about for user id=$userId")
        }.map {
            service.updateAbout(userId = userId, value = it.value)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun setAddress(
        request: Mono<UpdatePropertyRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        return request.doOnNext {
            log.info("Requested update address for user id=$userId")
        }.map {
            service.updateAddress(userId = userId, value = it.value)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun setGps(
        request: Mono<UpdatePropertyRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        return request.doOnNext {
            log.info("Requested update GPS for user id=$userId")
        }.map {
            service.updateGPS(userId = userId, value = it.value)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun setOpeningHours(
        request: Mono<UpdatePropertyRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        return request.doOnNext {
            log.info("Requested update opening hours for user id=$userId")
        }.map {
            service.updateOpeningHours(userId = userId, value = it.value)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun setAvatar(body: Mono<Resource>, exchange: ServerWebExchange?): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        return body.doOnNext {
            log.info("Requested set avatar for userId=$userId")
        }.map {
            service.updateAvatar(userId = userId, value = it.inputStream)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun deleteAvatar(exchange: ServerWebExchange?): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        service.deleteAvatar(userId = userId)
        return Mono.just(ResponseEntity.status(HttpStatus.OK).build())
    }

}