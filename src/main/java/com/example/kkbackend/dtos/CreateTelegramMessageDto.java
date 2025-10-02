package com.example.kkbackend.dtos;

public record CreateTelegramMessageDto(int messageId, String chatId, String eventId) {
}
