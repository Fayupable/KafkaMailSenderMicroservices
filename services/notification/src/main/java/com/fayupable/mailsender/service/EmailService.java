package com.fayupable.mailsender.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.fayupable.mailsender.enums.EmailTemplates.USER_LOGIN;
import static com.fayupable.mailsender.enums.EmailTemplates.USER_VERIFICATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements IEmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendUserVerificationCode(String destinationEmail, String verificationCode, LocalDateTime verificationExpiredDate) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper;

        try {
            messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name());
            messageHelper.setFrom("fayupable@gmail.com");
            messageHelper.setTo(destinationEmail);
            messageHelper.setSubject(USER_VERIFICATION.getSubject());

            final String templateName = USER_VERIFICATION.getTemplate();
            Map<String, Object> variables = new HashMap<>();
            variables.put("verificationCode", verificationCode);
            variables.put("verificationCodeExpiration", verificationExpiredDate);

            Context context = new Context();
            context.setVariables(variables);

            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            javaMailSender.send(mimeMessage);
            log.info("Email sent to {} with template {}", destinationEmail, templateName);

        } catch (MessagingException e) {
            log.error("MessagingException occurred while sending email to {}", destinationEmail, e);
            throw new RuntimeException("Failed to send email due to messaging error", e);
        } catch (MailException e) {
            log.error("MailException occurred while sending email to {}", destinationEmail, e);
            throw new RuntimeException("Failed to send email due to mail error", e);
        }
    }


    @Override
    @Async
    public void sendUserLogin(String destinationEmail, LocalDateTime loginTime) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper;

        try {
            messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name());
            messageHelper.setFrom("fayupable@gmail.com");
            messageHelper.setTo(destinationEmail);
            messageHelper.setSubject(USER_LOGIN.getSubject());

            final String templateName = USER_LOGIN.getTemplate();
            Map<String, Object> variables = new HashMap<>();
            variables.put("userLoginTime", loginTime);

            Context context = new Context();
            context.setVariables(variables);

            String htmlTemplate = templateEngine.process(templateName, context);
            messageHelper.setText(htmlTemplate, true);

            javaMailSender.send(mimeMessage);
            log.info("Email sent to {} with template {}", destinationEmail, templateName);

        } catch (MessagingException e) {
            log.error("MessagingException occurred while sending email to {}", destinationEmail, e);
            throw new RuntimeException("Failed to send email due to messaging error", e);
        } catch (MailException e) {
            log.error("MailException occurred while sending email to {}", destinationEmail, e);
            throw new RuntimeException("Failed to send email due to mail error", e);
        }
    }
}
