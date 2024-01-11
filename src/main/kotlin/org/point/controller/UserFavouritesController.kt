package org.point.controller

import org.point.openApiUserFavourites.controller.api.UserFavouritesApi
import org.point.openApiUserFavourites.controller.dto.AddFavouritesRequest
import org.point.openApiUserFavourites.controller.dto.Favourites
import org.point.service.TokenService
import org.point.service.UserFavouritesService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.UUID


@RestController
class UserFavouritesController(
    private val tokenService: TokenService,
    private val service: UserFavouritesService,
) : UserFavouritesApi {
    val log = LoggerFactory.getLogger(javaClass)

    override fun addFavourites(
        request: Mono<AddFavouritesRequest>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        return request.doOnNext {
            log.info("addFavourites for userId=$userId: performerId=${it.performerId}")
        }.map {
            // todo: validate
            service.addFavourites(userId = userId, performerId = it.performerId, name = it.name)
            ResponseEntity.status(HttpStatus.OK).build()
        }
    }

    override fun deleteFavourites(performerId: UUID, exchange: ServerWebExchange?): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        log.info("deleteFavourites for userId=$userId: performerId=$performerId")
        service.deleteFavourites(userId = userId, performerId = performerId)
        return Mono.just(ResponseEntity.status(HttpStatus.OK).build())
    }

    override fun getFavourites(exchange: ServerWebExchange?): Mono<ResponseEntity<Flux<Favourites>>> {
        val userId = tokenService.parseToken()
        log.info("getFavourites for userId=$userId")
        val res = service.getFavourites(userId = userId).map {
            Favourites()
                .id(it.id)
                .performerId(it.performerId)
                .name(it.name)
        }.toFlux()
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(res))
    }

}