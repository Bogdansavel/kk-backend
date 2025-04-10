package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record CreateRoundDto(long id, boolean isActive) {
}
