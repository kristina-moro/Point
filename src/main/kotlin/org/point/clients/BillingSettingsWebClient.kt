package org.point.clients

import com.fasterxml.jackson.databind.ObjectMapper
import org.point.config.ApplicationProperties
import org.point.exceptions.ApiError
import org.point.exceptions.CustomException
import org.point.exceptions.Errors
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.UUID

@Service
class BillingSettingsWebClient(
    properties: ApplicationProperties,
    private val objectMapper: ObjectMapper,
) {

    private val webClient: WebClient = WebClient.builder().baseUrl(properties.baseUrl).build()
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val SUITABLE_ROUTES_URL = "/internal/application-payment-methods/suitable-routes"

    // конвертируем заявленную клиентом сумму в валюту аккаунта платежной системы
    // вопрос: это всегда надо делать или только если ПС сама не может принять платеж в валюте клиента и сделать конвертацию самостоятельно?
    fun convertCurrency(sourceIsoCode: String, sourceAmount: BigDecimal, targetIsoCode: String): BigDecimal {
        // rest request в billing-settings  /currency/convert  (будет создан)
        if (sourceIsoCode == targetIsoCode) {
            return sourceAmount
        }
        return BigDecimal.valueOf(123.45) // todo: ?
    }

    fun getSuitableRoutes(
        applicationId: String,
        paymentMethodId: UUID,
        amount: BigDecimal,
        currency: String,
    ): Mono<SuitableRoutesResponse> {
        log.info(
            "getSuitableRoutes use application_id $applicationId, payment_method_id $paymentMethodId, " +
                    "payment_method_type , amount $amount, currency $currency"
        )

        return webClient.get()
            .uri { builder ->
                builder
                    .path(SUITABLE_ROUTES_URL)
                    .queryParam("application_id", applicationId)
                    .queryParam("payment_method_id", paymentMethodId)
                    .queryParam("amount", amount)
                    .queryParam("currency", currency)
                    .build()
            }
            .retrieve()
            /*.onStatus(
                { httpStatus -> HttpStatus.OK != httpStatus },
                {
                    it.bodyToMono(String::class.java).map { response ->
                        log.error("Failed to send request to billing-settings: $response")
                        throw response.toCustomException(objectMapper)
                    }
                }
            )*/
            .bodyToMono(SuitableRoutesResponse::class.java)
            .handle { routes, sink ->
                if (routes.routes.isEmpty()) {
                    sink.error(
                        CustomException(
                            ApiError(code = Errors.INTERNAL_ERROR.name, message = Errors.INTERNAL_ERROR.message)
                        )
                    )
                } else {
                    sink.next(routes)
                }
            }
    }
}

data class SuitableRoutesResponse(
    val routes: List<PaymentRoute> = emptyList()
)

data class PaymentRoute(
    var id: UUID,
    val paymentSystemId: UUID,
    val paymentSystemName: String,
    val priority: Int,
    val paymentSystemAccountId: UUID,
    val currencyAccountId: UUID,
    val currency: String,
)

