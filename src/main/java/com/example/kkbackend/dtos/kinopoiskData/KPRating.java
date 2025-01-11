package com.example.kkbackend.dtos.kinopoiskData;

public record KPRating(
        float kp,
        float imdb,
        float filmCritics,
        float russianFilmCritics,
        boolean await
) {
}
