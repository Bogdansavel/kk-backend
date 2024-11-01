package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record RegisterResponseDto(int membersCount, String message, boolean isAlreadyRegistered,
                                  List<TelegramMessageDto> messages) {
}
