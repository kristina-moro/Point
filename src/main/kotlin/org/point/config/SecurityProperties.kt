package org.point.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "security")
data class SecurityProperties(
    var publicKey: String?,
    var privateKey: String?,
    var disable: Boolean?
)
