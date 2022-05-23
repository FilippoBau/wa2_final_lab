package it.polito.wa2.group22.server.services

import it.polito.wa2.group22.server.exceptions.EmailServiceException
import it.polito.wa2.group22.server.utils.EmailResult
import it.polito.wa2.group22.server.utils.emailResultToMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class EmailService {

    @Autowired
    lateinit var mailSender: JavaMailSender

    fun sendMail(emailAddress: String, username: String, activationCode: String, expiration: Date) {
        if (username == "") {
            throw EmailServiceException(emailResultToMessage[EmailResult.MISSING_USERNAME]!!)
        }
        if (activationCode == "") {
            throw EmailServiceException(emailResultToMessage[EmailResult.MISSING_ACT_CODE]!!)
        }
        if (emailAddress == "") {
            throw EmailServiceException(emailResultToMessage[EmailResult.MISSING_EMAIL]!!)
        }

        val emailMessage = SimpleMailMessage()
        emailMessage.setFrom("group22wa2@gmail.com")
        emailMessage.setTo(emailAddress)
        emailMessage.setText(
            "Hello $username, \n"
                    + "here's your activation code: \n\n"
                    + "$activationCode \n\nThe activation code is valid until: ${
                SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss"
                ).format(expiration)
            }.\n" + "Otherwise you can use the following link: <a href='https://www.youtube.com/watch?v=dQw4w9WgXcQ'>prova</a>"
        )
        emailMessage.setSubject("Activation code group22")
        mailSender.send(emailMessage)
    }
}