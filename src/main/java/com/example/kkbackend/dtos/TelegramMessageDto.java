package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record TelegramMessageDto(int chatId, int messageId) {
}
