package org.point.controller


import org.point.openApiUserPortfolio.controller.api.UserPortfolioApi
import org.point.openApiUserPortfolio.controller.dto.GetPortfolioResponse
import org.point.openApiUserPortfolio.controller.dto.ImageInformation
import org.point.openApiUserPortfolio.controller.dto.PortfolioItem
import org.point.openApiUserPortfolio.controller.dto.UploadImageResponse
import org.point.service.TokenService
import org.point.service.UserPortfolioService
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID


@RestController
class UserPortfolioController(
    private val tokenService: TokenService,
    private val service: UserPortfolioService,
) : UserPortfolioApi {
    val log = LoggerFactory.getLogger(javaClass)

    override fun uploadImage(body: Mono<Resource>, exchange: ServerWebExchange?): Mono<ResponseEntity<UploadImageResponse>> {
        val userId = tokenService.parseToken()
        return body.doOnNext {
            log.info("Upload portfolio image for userId=$userId")
        }.map {
            val imageId = service.uploadImage(userId = userId, value = it.inputStream)
            ResponseEntity.status(HttpStatus.OK).body(UploadImageResponse().id(imageId))
        }
    }

    override fun deleteImage(id: Int, exchange: ServerWebExchange?): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        log.info("Delete portfolio image id=$id for userId=$userId")
        service.deleteImage(userId = userId, imageId = id)
        return Mono.just(ResponseEntity.status(HttpStatus.OK).build())
    }

    override fun editDescriptionAndOrder(
        imageInformation: Flux<ImageInformation>,
        exchange: ServerWebExchange?
    ): Mono<ResponseEntity<Void>> {
        val userId = tokenService.parseToken()
        log.info("Update portfolio description for userId=$userId")
        return imageInformation.collectList().flatMap {
            service.updateDescriptionAndOrder(userId = userId, imageInformation = it)
            Mono.just(ResponseEntity.status(HttpStatus.OK).build())
        }
    }

    override fun getPortfolio(userId: UUID, exchange: ServerWebExchange?): Mono<ResponseEntity<GetPortfolioResponse>> {
        log.info("Update portfolio description for userId=$userId")
        val items = service.get(userId = userId)
            .map {
                PortfolioItem().id(it.id).description(it.description).image(it.data).sortOrder(it.sortOrder)
            }

        return Mono.just(
            ResponseEntity.status(HttpStatus.OK)
                .body(GetPortfolioResponse().items(items))
        )
    }
}