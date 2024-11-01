package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record TelegramMessageDto(String chatId, int messageId) {
}
