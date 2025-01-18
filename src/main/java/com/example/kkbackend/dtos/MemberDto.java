package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record MemberDto(Double telegramId, String username, String firstName, boolean freshBlood) {
}
