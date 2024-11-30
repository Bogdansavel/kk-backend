package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record MemberDto(Integer telegramId, String username, String firstName, boolean freshBlood) {
}
