package com.fayupable.mailsender.kafka;

import com.fayupable.mailsender.entity.Notification;
import com.fayupable.mailsender.enums.NotificationType;
import com.fayupable.mailsender.kafka.user.UserConfirmation;
import com.fayupable.mailsender.repository.INotificationRepository;
import com.fayupable.mailsender.service.IEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final IEmailService emailService;
    private final INotificationRepository notificationRepository;

    @KafkaListener(
            topics = "user-confirmation-topic",
            groupId = "user-group",
            containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consumeUserConfirmation(UserConfirmation userConfirmation) {
        log.info("Consuming user confirmation message: {}", userConfirmation);
        saveUserConfirmation(userConfirmation);
        try {
            sendUserConfirmationEmail(userConfirmation);
        } catch (MessagingException e) {
            log.error("MessagingException occurred while sending user confirmation email to user {}", userConfirmation.getEmail(), e);
            throw new RuntimeException("Failed to send user confirmation email due to messaging error", e);
        }

    }

    private void saveUserConfirmation(UserConfirmation userConfirmation) {
        log.info("Saving user confirmation notification for user {}", userConfirmation.getEmail());
        notificationRepository.save(
                Notification.builder()
                        .notificationType(NotificationType.USER_VERIFICATION)
                        .sendAt(LocalDateTime.now())
                        .build()
        );
        log.info("User confirmation notification saved for user {}", userConfirmation.getEmail());
    }

    private void sendUserConfirmationEmail(UserConfirmation userConfirmation) throws MessagingException {
        log.info("Sending user confirmation email to user {}", userConfirmation.getEmail());
        emailService.sendUserVerificationCode(userConfirmation.getEmail(), userConfirmation.getVerificationCode(), userConfirmation.getVerificationCodeExpiration());
        log.info("User confirmation email sent to user {}", userConfirmation.getEmail());
    }


    @KafkaListener(
            topics = "user-login-topic",
            groupId = "user-group",
            containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consumeUserLogin(UserConfirmation userLogin) {
        log.info("Consuming user login message: {}", userLogin);
        saveUserLogin(userLogin);
        try {
            sendUserLoginEmail(userLogin);
        } catch (MessagingException e) {
            log.error("MessagingException occurred while sending user login email to user {}", userLogin.getEmail(), e);
            throw new RuntimeException("Failed to send user login email due to messaging error", e);
        }

    }

    private void saveUserLogin(UserConfirmation userLogin) {
        log.info("Saving user login notification for user {}", userLogin.getEmail());
        notificationRepository.save(
                Notification.builder()
                        .notificationType(NotificationType.USER_LOGIN)
                        .sendAt(LocalDateTime.now())
                        .build()
        );

    }

    private void sendUserLoginEmail(UserConfirmation userLogin) throws MessagingException {
        log.info("Sending user login email to user {}", userLogin.getEmail());
        emailService.sendUserLogin(userLogin.getEmail(), userLogin.getUserLoginTime());
        log.info("User login email sent to user {}", userLogin.getEmail());


    }
}
