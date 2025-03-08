package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record MovieDto(
        UUID id,
        int kinopoiskId,
        String name,
        List<RateDto> ratings,
        String ratePhotoName,
        String posterUrl,
        int averageRating,
        MemberDto member) {
}
