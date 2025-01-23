package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record MessageDto(String id, String chatId, int messageId, String eventId, String description, String posterUrl) {
}
