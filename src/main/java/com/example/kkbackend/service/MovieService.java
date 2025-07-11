package com.example.kkbackend.service;

import com.example.kkbackend.entities.Movie;

import java.util.Optional;
import java.util.UUID;

public interface MovieService {
    Movie getById(UUID id);
    Movie postMovie(Movie movie, long roundId, Optional<Double> telegramId, Optional<String> username);
}
