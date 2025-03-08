package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record EventDto(
        UUID id,
        String movieId,
        String language,
        String date,
        List<TelegramMessageDto> messages,
        List<MemberDto> members,
        String description,
        String posterUrl) {
}
