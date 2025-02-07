package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record RegisterResponseDto(int membersCount, String message, boolean isAlreadyRegistered, boolean limitIsExceeded,
                                  List<TelegramMessageDto> messages, List<MemberDto> members) {
}
