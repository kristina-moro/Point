package org.point

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(exclude = [R2dbcAutoConfiguration::class])
@EnableScheduling
@EnableConfigurationProperties
class PointApplication

fun main(args: Array<String>) {
    runApplication<PointApplication>(*args)
}
