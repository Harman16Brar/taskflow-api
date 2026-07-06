package com.taskflow_api.shared.email;

import com.taskflow_api.task.Task;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendWelcomeEmail(String to, String firstName) {
        Context context = new Context();
        context.setVariable("firstName", firstName);
        String html = templateEngine.process("email/welcome", context);
        sendEmail(to, "Welcome to TaskFlow!", html);
    }

    @Async
    public void sendTaskAssignedEmail(String to, String taskName, String projectName, String priority) {
        Context context = new Context();
        context.setVariable("taskName", taskName);
        context.setVariable("projectName", projectName);
        context.setVariable("priority", priority);
        String html = templateEngine.process("email/task-assigned", context);
        sendEmail(to, "You've been assigned a task: " + taskName, html);
    }

    @Async
    public void sendOverdueTasksEmail(String to, List<Task> tasks) {
        Context context = new Context();
        context.setVariable("tasks", tasks);
        String html = templateEngine.process("email/overdue-tasks", context);
        sendEmail(to, "Overdue Tasks Summary", html);
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}