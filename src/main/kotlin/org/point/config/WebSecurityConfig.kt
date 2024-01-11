package org.point.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@Profile("!test")
class WebSecurityConfig : WebFluxConfigurer {

    @Value("\${spring.security.oauth2.client.provider.oidc.issuer-uri}")
    private lateinit var issuerUri: String

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf()
            .disable()
            .authorizeExchange()
            .anyExchange().permitAll()
            .and()

        http.oauth2Client()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtDecoder(jwtDecoder())

        return http.build()
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri) as NimbusReactiveJwtDecoder
    }
}

/*import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@Profile("!test")
class WebSecurityConfig : WebFluxConfigurer {

    @Value("\${spring.security.oauth2.client.provider.oidc.issuer-uri}")
    private lateinit var issuerUri: String

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .csrf()
            .disable()
            .authorizeExchange()
            .anyExchange().permitAll()
            .and()

        http.oauth2Client()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .jwtDecoder(jwtDecoder())

        return http.build()
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(issuerUri) as NimbusReactiveJwtDecoder
    }
}*/

