package com.example.kkbackend.dtos;

import lombok.Builder;

@Builder
public record EventMovieDto (MovieDto movie, String language, String date) {
}
