package org.point.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties //это нужно?
@ConfigurationProperties(prefix = "point")
data class ApplicationProperties(
    var baseUrl: String = "",
    var portfolioMaxSize: Int = 1,
)