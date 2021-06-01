package com.prismhealth.services;

import com.prismhealth.Models.Mail;
import com.prismhealth.config.Constants;
import com.prismhealth.repository.MailService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {
    private final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    private final JavaMailSender mailSender;

    public void sendEmail(Mail mail) {
        try {

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(Constants.email);
            simpleMailMessage.setTo(mail.getMailTo());
            simpleMailMessage.setSubject(mail.getMailSubject());
            simpleMailMessage.setText(mail.getMailContent());
            mailSender.send(simpleMailMessage);
        }catch (Exception ex){
            log.error(" mail Error " + ex);
        }
    }
}
