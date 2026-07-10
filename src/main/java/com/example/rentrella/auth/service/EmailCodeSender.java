package com.example.rentrella.auth.service;

import com.example.rentrella.auth.domain.EmailVerificationPurpose;
import com.example.rentrella.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailCodeSender {

    private final ObjectProvider<JavaMailSender> javaMailSenderProvider;

    @Value("${app.mail.from:no-reply@rentrella.local}")
    private String from;

    public void send(String email, EmailVerificationPurpose purpose, String code) {
        JavaMailSender javaMailSender = javaMailSenderProvider.getIfAvailable();
        if (javaMailSender == null) {
            throw new AuthException(HttpStatus.SERVICE_UNAVAILABLE, "Email sender is not configured.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject(subject(purpose));
            message.setText("Verification code: " + code + "\nThis code expires in 3 minutes.");
            javaMailSender.send(message);
        } catch (MailException exception) {
            throw new AuthException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to send verification email.");
        }
    }

    private String subject(EmailVerificationPurpose purpose) {
        if (purpose == EmailVerificationPurpose.PASSWORD_RESET) {
            return "Rentrella password reset verification code";
        }
        return "Rentrella signup verification code";
    }
}
