package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record RateDto(String id, int telegramId, String username, String firstName, String movieId, int rating, boolean liked, boolean discussable) {
}
