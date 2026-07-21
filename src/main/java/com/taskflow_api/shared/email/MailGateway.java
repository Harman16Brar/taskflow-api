package com.taskflow_api.shared.email;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailGateway {
    private final JavaMailSender mailSender;

    @CircuitBreaker(name = "email", fallbackMethod = "sendFallback")
    @Retry(name = "email")
    public void send(MimeMessage message, String to, String subject) {
        mailSender.send(message);
        log.info("Email sent to {}: {}", to, subject);
    }

    private void sendFallback(MimeMessage message, String to, String subject, Exception ex) {
        log.error("Email delivery failed after retries — dropping message to {}: {}", to, ex.getMessage());
    }

}
