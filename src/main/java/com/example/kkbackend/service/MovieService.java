package com.example.kkbackend.service;

import com.example.kkbackend.entities.Movie;

import java.util.UUID;

public interface MovieService {
    Movie getById(UUID id);
    Movie postMovie(Movie movie, long roundId, UUID memberId);
}
