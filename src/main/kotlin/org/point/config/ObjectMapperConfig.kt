package org.point.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class ObjectMapperConfig {

    @Bean
    @Primary
    fun objectMapper() = ObjectMapper().apply {
        /*registerModule(KotlinModule.Builder().build()).propertyNamingStrategy = BooleanNamingStrategy()
        registerModule(
            SimpleModule()
                .addDeserializer(Boolean::class.javaPrimitiveType, CustomBooleanDeserializer())
                .addDeserializer(Boolean::class.javaObjectType, CustomBooleanDeserializer())
        )*/
        registerModule(JavaTimeModule())
    }

}