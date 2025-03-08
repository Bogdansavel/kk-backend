package com.example.kkbackend.dtos;

import java.util.UUID;

public record CreateEventDto(
        UUID movieId,
        String language,
        String date,
        String posterUrl) {
}
