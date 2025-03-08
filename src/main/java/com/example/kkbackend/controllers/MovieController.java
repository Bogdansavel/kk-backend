package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.MovieDto;
import com.example.kkbackend.dtos.MovieWithKinopoiskDataDto;
import com.example.kkbackend.entities.Member;
import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.dtos.CreateMovieDto;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.repositories.MemberRepository;
import com.example.kkbackend.repositories.MovieRepository;
import com.example.kkbackend.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieRepository movieRepository;
    private final MemberService memberService;

    @PostMapping
    public MovieDto postMovie(@RequestBody CreateMovieDto createMovieDto) {
        return fromMovieToDto(movieRepository.save(
                fromDtoToMovie(
                        createMovieDto, memberService.getById(createMovieDto.memberId())
                ))
        );
    }

    @GetMapping("{id}")
    public MovieDto getMovieById(@PathVariable String id) {
        return fromMovieToDto(movieRepository.getReferenceById(UUID.fromString(id)));
    }

    public static Movie fromDtoToMovie(CreateMovieDto createMovieDto, Member member) {
        return Movie.builder()
                .kinopoiskId(createMovieDto.kinopoiskId())
                .name(createMovieDto.name())
                .ratePhotoName(createMovieDto.ratePhotoName())
                .posterUrl(createMovieDto.posterUrl())
                .ratings(new ArrayList<>())
                .member(member)
                .build();
    }

    public static MovieDto fromMovieToDto(Movie movie) {
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
                .build();
    }
}
