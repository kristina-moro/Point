package org.point.config

import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfig {
    @Bean
    fun publicUserApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("All")
            .pathsToMatch("/dictionary/**", "/user/**", "/schedule/**")
            .build()
    }


}
