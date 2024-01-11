package org.point.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.mail.internet.MimeMessage

@Service
class EmailSender(private val sender: JavaMailSender) {

    fun send(to: String, subject: String, what: String) {
        val message = SimpleMailMessage()

        message.setFrom("team@point.com")
        message.setTo(to)
        message.setSubject(subject)
        message.setText(what)

        sender.send(message)
    }

    fun sendHtmlMessage(to: String, subject: String, htmlBody: String) {
        val message: MimeMessage = sender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(htmlBody, true)
        sender.send(message)
    }

}