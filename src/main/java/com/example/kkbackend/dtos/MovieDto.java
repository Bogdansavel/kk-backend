package com.example.kkbackend.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record MovieDto(String id, int kinopoiskId, String name, List<RateDto> ratings, String photoName,
                       String posterUrl, int averageRating, MemberDto memberDto) {
}
