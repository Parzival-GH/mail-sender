package com.example.mailsender.services;

import com.example.mailsender.exceptions.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.List;
import java.util.Objects;

@Service
public class LetterSenderService {

    private static final Logger logger = LoggerFactory.getLogger(LetterSenderService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmailWithAttachmentsAndTemplate(String to, String subject, String templateName, Context context, List<String> pathsToAttachments) {

        if (pathsToAttachments == null || pathsToAttachments.isEmpty()) {
            logger.warn("No attachments provided, proceeding without attachments.");
        }

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            if (pathsToAttachments != null) {
                for (String pathToAttachment : pathsToAttachments) {
                    FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
                    helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
                }
            }

            emailSender.send(message);
            logger.info("Email sent successfully to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new EmailSendException("Failed to send email to " + to, e);
        }
    }

}
