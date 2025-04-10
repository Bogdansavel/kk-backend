package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.Set;

@Builder
public record RoundDto(
        long id,
        Set<MovieDto> movies,
        TelegramMessageDto message,
        boolean isActive
) {
}
