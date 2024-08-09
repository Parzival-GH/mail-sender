package com.example.mailsender.controller;

import com.example.mailsender.dto.EmailRequestDto;
import com.example.mailsender.exceptions.EmailSendException;
import com.example.mailsender.services.LetterSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class LetterSenderController {

    private final LetterSenderService letterSenderService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDto emailRequestDto) {
        try {
            Context context = new Context();
            context.setVariable("message", emailRequestDto.message());

            log.info("Sending email to: {}, subject: {}", emailRequestDto.to(), emailRequestDto.subject());

            letterSenderService.sendEmailWithAttachmentsAndTemplate(
                    emailRequestDto.to(),
                    emailRequestDto.subject(),
                    "emailTemplate",
                    context,
                    emailRequestDto.attachments());

            return ResponseEntity.ok("Email sent successfully");
        } catch (EmailSendException e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending email: " + e.getMessage());
        }
    }
}
