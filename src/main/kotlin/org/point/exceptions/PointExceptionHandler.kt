package org.point.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@ControllerAdvice
class PointExceptionHandler {
    val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ServerWebInputException::class)
    fun serverWebInputExceptionHandler(
        ex: ServerWebInputException,
        request: ServerWebExchange
    ): Mono<ResponseEntity<ApiError>> {
        log.error("Handled ServerWebInputException: ${ex.message}"/*, ex*/) // TODO:

        return Mono.just(
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError(code = Errors.BAD_REQUEST.name, message = Errors.BAD_REQUEST.message))
        )
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun webExchangeBindExceptionHandler(
        ex: WebExchangeBindException,
        request: ServerWebExchange
    ): Mono<ResponseEntity<ApiError>> {
        log.error("Handled WebExchangeBindException: ${ex.message}"/*, ex*/) // TODO:

        val (message, code) = ex.bindingResult.toErrorMessageAndCode()
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError(code = code, message = message))
        )
    }

    @ExceptionHandler
    fun customException(
        ex: CustomException,
        request: ServerWebExchange
    ): Mono<ResponseEntity<ApiError>> {
        log.error("Handled CustomException: ${ex.message}"/*, ex*/) // TODO:
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ex.error)
        )
    }

    @ExceptionHandler
    fun handleNotFoundException(
        ex: NotFoundException,
        request: ServerWebExchange
    ): Mono<ResponseEntity<ApiError>> {
        log.error("Handled NotFoundException: ${ex.message}"/*, ex*/) // TODO:
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.error)
        )
    }

    @ExceptionHandler
    fun handleException(
        ex: Exception,
        request: ServerWebExchange
    ): Mono<ResponseEntity<ApiError>> {
        log.error("Handled Exception: ${ex.message}"/*, ex*/) // TODO:
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError(code = Errors.INTERNAL_ERROR.name, message = Errors.INTERNAL_ERROR.message))
        )
    }

    // TODO: это нужно ?
    private fun BindingResult.toErrorMessageAndCode(): Pair<String, String> {
        val sb = StringBuilder()
        var errorCode = Errors.BAD_REQUEST.name
        if (hasFieldErrors()) {
            if (fieldErrors.size == 1) {
                errorCode = "INVALID_${fieldErrors[0].field.toUpperSnakeCase()}"
            }
            sb.append("Invalid parameters: " + fieldErrors.joinToString { it.field })
        }
        if (hasGlobalErrors()) {
            sb.append(globalErrors.joinToString { it.toString() })
        }
        if (sb.isEmpty()) {
            sb.append(Errors.BAD_REQUEST.message)
        }
        return sb.toString() to errorCode
    }

    private fun String.toUpperSnakeCase() = uppercase().replace(".", "_")
}
