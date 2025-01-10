package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.MovieDto;
import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.dtos.CreateMovieDto;
import com.example.kkbackend.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieRepository movieRepository;

    @PostMapping
    public MovieDto postMovie(@RequestBody CreateMovieDto createMovieDto) {
        return fromMovieToDto(movieRepository.save(
                Movie.builder()
                        .kinopoiskId(createMovieDto.kinopoiskId())
                        .name(createMovieDto.name())
                        .ratings(new ArrayList<>())
                        .posterUrl(createMovieDto.posterUrl())
                        .build()));
    }

    @GetMapping("{id}")
    public MovieDto getMovieById(@PathVariable String id) {
        return fromMovieToDto(movieRepository.getReferenceById(UUID.fromString(id)));
    }

    public static MovieDto fromMovieToDto(Movie movie) {
        return MovieDto.builder()
                .id(movie.getId().toString())
                .kinopoiskId(movie.getKinopoiskId())
                .name(movie.getName())
                .ratings(movie.getRatings().stream().map(RateController::fromRateToDto)
                        .collect(Collectors.toList()))
                .photoName(movie.getPhotoName())
                .posterUrl(movie.getPosterUrl())
                .averageRating(movie.averageRating())
                .build();
    }
}
