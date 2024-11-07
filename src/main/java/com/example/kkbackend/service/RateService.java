package com.example.kkbackend.service;

import com.example.kkbackend.entities.Rate;

import java.util.UUID;

public interface RateService {
    Rate getRateByMovieIdAndUsername(UUID movieId, String username);
}
