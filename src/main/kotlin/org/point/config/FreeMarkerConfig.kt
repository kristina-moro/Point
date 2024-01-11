package org.point.config

import freemarker.cache.ClassTemplateLoader
import freemarker.cache.TemplateLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer


@Configuration
class FreeMarkerConfig {
    @Bean
    fun freemarkerClassLoaderConfig(): FreeMarkerConfigurer {
        val configuration = freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_27)
        val templateLoader: TemplateLoader = ClassTemplateLoader(this.javaClass, "/mail-templates")
        configuration.templateLoader = templateLoader
        val freeMarkerConfigurer = FreeMarkerConfigurer()
        freeMarkerConfigurer.configuration = configuration
        return freeMarkerConfigurer
    }
}