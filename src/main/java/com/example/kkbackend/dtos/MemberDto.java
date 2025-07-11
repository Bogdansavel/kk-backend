package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.Optional;

@Builder
public record MemberDto(Double telegramId, String username, String firstName, boolean freshBlood) {
}
