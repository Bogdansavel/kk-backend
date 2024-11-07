package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record AverageRateDto(String movieName, int rating) {
}
