package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.AverageRateDto;
import com.example.kkbackend.dtos.RateDto;
import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.entities.Rate;
import com.example.kkbackend.repositories.MovieRepository;
import com.example.kkbackend.repositories.RateRepository;
import com.example.kkbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/rate")
@RequiredArgsConstructor
public class RateController {
    private final RateRepository rateRepository;
    private final MovieRepository movieRepository;
    private final MemberService memberService;

    @PostMapping
    public RateDto postRate(@RequestBody RateDto rateDto) {
        var movie = movieRepository.getReferenceById(UUID.fromString(rateDto.movieId()));
        var member = memberService.getMemberByUsername(rateDto.username());
        return fromRateToDto(
                rateRepository.save(
                    Rate.builder()
                            .rating(rateDto.rating())
                            .liked(rateDto.liked())
                            .discussable(rateDto.discussable())
                            .movie(movie)
                            .member(member)
                            .build()
        ));
    }

    @GetMapping("/average/{movieId}")
    public AverageRateDto getAverage(@PathVariable String movieId) {
        var movie = movieRepository.getReferenceById(UUID.fromString(movieId));
        return AverageRateDto.builder()
                .movieName(movie.getName())
                .rating(countAverageRating(movie))
                .build();
    }

    private int countAverageRating(Movie movie) {
        var ratings = movie.getRatings();
        if (ratings.isEmpty()) {
            return 0;
        }
        return ratings.stream().map(Rate::getRating).mapToInt(Integer::intValue).sum() / ratings.size();
    }

    private RateDto fromRateToDto(Rate rate) {
        return RateDto.builder()
                .rating(rate.getRating())
                .liked(rate.isLiked())
                .discussable(rate.isDiscussable())
                .username(rate.getMember().getUserName())
                .movieId(rate.getMovie().getId().toString())
                .build();
    }
}
