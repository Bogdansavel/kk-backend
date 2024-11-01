package com.example.kkbackend.controllers;

import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.dtos.CreateMovieDto;
import com.example.kkbackend.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieRepository movieRepository;

    @PostMapping
    public Movie postMovie(@RequestBody CreateMovieDto createMovieDto) {
        return movieRepository.save(Movie.builder().kinopoiskId(createMovieDto.kinopoiskId()).build());
    }
}
