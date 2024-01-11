package org.point.service

import freemarker.template.Template
import org.point.domain.User
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer


@Service
class Mailer(private val config: FreeMarkerConfigurer, private val sender: EmailSender) {

    // если авторизуемся через свою почту
    fun sendEmailConfirmationEmail(login: String, userName: String, lang: String, uri: String) {
        val template = templateOf("EmailConfirmation.html") // todo: use lang
        val params = mapOf(
            "userName" to userName,
            "email" to login,
            "link" to uri,
            "senderName" to "Point team")
        val htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, params)

        sender.sendHtmlMessage(to = login, subject = "Welcome to POINT", htmlBody = htmlBody)
    }

    // если авторизуемся через аккаунт в соцсетях и т.п.
    fun sendWelcomeEmail(user: User) {
        val template = templateOf("Hello.html")
        val params = mapOf(
            "recipient" to user.name,
            "text" to "Welcome to Point Application",
            "senderName" to "Point team"
        )
        val htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, params)

        sender.sendHtmlMessage(to = user.login, subject = "Welcome to POINT", htmlBody = htmlBody)
    }

    private fun templateOf(name: String): Template {
        return config.getConfiguration().getTemplate(name)
    }
}