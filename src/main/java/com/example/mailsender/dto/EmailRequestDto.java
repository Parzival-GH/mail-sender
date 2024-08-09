package com.example.mailsender.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EmailRequestDto(
        String to,
        String subject,
        String message,
        List<String> attachments
) {}

