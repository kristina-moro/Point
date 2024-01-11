package org.point.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.point.domain.Category
import org.point.service.TokenService
import org.point.service.UserCategoryService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/user/category")
@Tag(name = "UserCategory", description = "2. Пользователи: области деятельности исполнителей")
class UserCategoryController(
    private val tokenService: TokenService,
    private val service: UserCategoryService
) {
    val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Категории пользователя")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUserCategory(): List<Category> {
        val userId = tokenService.parseToken()
        log.info("Get category by userId=$userId")
        return service.getUserCategory(userId = userId)
    }

    @Operation(summary = "Добавить категорию пользователю")
    @PostMapping("{cat_id}/add")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun addCategory(
        @Parameter(
            name = "cat_id",
            description = "Идентификатор категории",
            example = "113"
        ) @Valid @PathVariable(value = "cat_id", required = true) categoryId: Int
    ): ResponseEntity<Void> {
        val userId = tokenService.parseToken()
        log.info("Add category id=$categoryId to userId=$userId")
        service.addCategory(userId = userId, categoryId = categoryId)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    @Operation(summary = "Исключить категорию пользователя")
    @PostMapping("{cat_id}/remove")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun removeCategory(
        @Parameter(
            name = "cat_id",
            description = "Идентификатор категории",
            example = "113"
        ) @Valid @PathVariable(value = "cat_id", required = true) categoryId: Int
    ): ResponseEntity<Void> {
        val userId = tokenService.parseToken()
        log.info("Remove category id=$categoryId to userId=$userId")
        service.removeCategory(userId = userId, categoryId = categoryId)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

}