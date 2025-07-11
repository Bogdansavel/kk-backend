package com.example.kkbackend.dtos;

import java.util.Optional;

public record CreateMovieDto(
        int kinopoiskId,
        String name,
        String ratePhotoName,
        String posterUrl,
        Optional<Double> telegramId,
        Optional<String> username,
        Long round) {}
