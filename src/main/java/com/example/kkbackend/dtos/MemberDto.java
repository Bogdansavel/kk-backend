package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record MemberDto(String username, boolean freshBlood) {
}
