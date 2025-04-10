package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record SetReadyDto(double telegramId, boolean isReady) {
}
