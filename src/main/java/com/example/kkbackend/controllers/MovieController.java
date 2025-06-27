package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.MovieDto;
import com.example.kkbackend.dtos.MovieWithKinopoiskDataDto;
import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.dtos.CreateMovieDto;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.service.MovieService;
import com.example.kkbackend.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final RoundService roundService;

    @PostMapping
    public MovieDto postMovie(@RequestBody CreateMovieDto createMovieDto) {
        return fromMovieToDto(movieService.postMovie(
                fromDtoToMovie(createMovieDto), createMovieDto.round(), createMovieDto.memberId()));
    }

    @GetMapping("{id}")
    public MovieDto getMovieById(@PathVariable UUID id) {
        return fromMovieToDto(movieService.getById(id));
    }

    @GetMapping("/ready")
    public List<MovieWithKinopoiskDataDto> getReadyMovies() {
        var round = roundService.getLastActiveRound();
        return round.getMovies().stream().filter(Movie::getIsReady)
                .map(MovieController::fromMovieToWithKinopoiskDataDto)
                .collect(Collectors.toList());
    }

    public static Movie fromDtoToMovie(CreateMovieDto createMovieDto) {
        return Movie.builder()
                .kinopoiskId(createMovieDto.kinopoiskId())
                .name(createMovieDto.name())
                .ratePhotoName(createMovieDto.ratePhotoName())
                .posterUrl(createMovieDto.posterUrl())
                .ratings(new ArrayList<>())
                .build();
    }

    public static MovieDto fromMovieToDto(Movie movie) {
        Long roundId = null;
        if (movie.getRound() != null) {
            roundId = movie.getRound().getId();
        }

        return MovieDto.builder()
                .id(movie.getId())
                .kinopoiskId(movie.getKinopoiskId())
                .name(movie.getName())
                .ratings(movie.getRatings().stream().map(RateController::fromRateToDto)
                        .collect(Collectors.toList()))
                .ratePhotoName(movie.getRatePhotoName())
                .posterUrl(movie.getPosterUrl())
                .averageRating(movie.averageRating())
                .member(MemberMapper.toDto(movie.getMember()))
                .round(roundId)
                .isReady(movie.getIsReady())
                .kinopoiskData(movie.getKinopoiskData())
                .build();
    }

    public static MovieWithKinopoiskDataDto fromMovieToWithKinopoiskDataDto(Movie movie) {
        return MovieWithKinopoiskDataDto.builder()
                .id(movie.getId().toString())
                .kinopoiskId(movie.getKinopoiskId())
                .name(movie.getName())
                .ratings(movie.getRatings().stream().map(RateController::fromRateToDto)
                        .collect(Collectors.toList()))
                .posterUrl(movie.getPosterUrl())
                .averageRating(movie.averageRating())
                .kinopoiskData(movie.getKinopoiskData())
                .language(movie.getLanguage())
                .build();
    }
}
