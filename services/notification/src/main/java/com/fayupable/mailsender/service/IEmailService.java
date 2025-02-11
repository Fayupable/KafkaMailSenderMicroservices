package com.fayupable.mailsender.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;

public interface IEmailService {
    @Async
    void sendUserVerificationCode(String destinationEmail, String verificationCode, LocalDateTime verificationExpiredDate) throws MessagingException;


    void sendUserLogin(String destinationEmail, LocalDateTime loginTime) throws MessagingException;
}
