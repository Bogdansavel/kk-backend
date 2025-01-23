package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record EventDto(String movieId, String language, String date,
                       List<TelegramMessageDto> messages, List<MemberDto> members,
                       String description, String posterUrl) {
}
