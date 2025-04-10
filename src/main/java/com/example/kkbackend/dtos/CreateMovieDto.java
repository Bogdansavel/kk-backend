package com.example.kkbackend.dtos;

import java.util.UUID;

public record CreateMovieDto(
        int kinopoiskId,
        String name,
        String ratePhotoName,
        String posterUrl,
        UUID memberId,
        Long round) {}
