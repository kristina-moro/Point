package org.point.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Hooks
import javax.annotation.PostConstruct

@Configuration
class ReactorConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun onErrorDroppedHook() {
        Hooks.onErrorDropped { s -> log.warn(" onErrorDropped: $s") }
    }
}