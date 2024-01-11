package org.point.controller


import org.point.openApiDictionary.controller.api.DictionaryApi
import org.point.openApiDictionary.controller.dto.Category
import org.point.service.DictionaryService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@RestController
//@RequestMapping("/dictionary")
class DictionaryController(
    private val service: DictionaryService
) : DictionaryApi {
    val log = LoggerFactory.getLogger(javaClass)

    /*@Operation(
        summary = "Каталог услуг",
    )
    @GetMapping("/catalog")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getCatalog(
        @Parameter(
            name = "cat_id",
            description = "Идентификатор категории",
            example = "5"
        ) @Valid @RequestParam(value = "cat_id", required = false) categoryId: Int?,
    ): List<Category> {
        log.info("Categories requested by categoryId=$categoryId")
        return service.getCatalog(categoryId)
    }*/


    override fun getCategory(categoryId: Int, exchange: ServerWebExchange?): Mono<ResponseEntity<Flux<Category>>> {
        log.info("Categories requested by categoryId=$categoryId")
        val cats = service.getCatalog(categoryId).map {
            Category()
                .id(it.id)
                .name(it.name)
                .description(it.description)
                .parentId(it.parentId)
        }.toFlux()
        return Mono.just(ResponseEntity.status(HttpStatus.OK).body(cats))
    }


}