package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.AverageRateDto;
import com.example.kkbackend.dtos.RateDto;
import com.example.kkbackend.entities.Member;
import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.entities.Rate;
import com.example.kkbackend.repositories.MemberRepository;
import com.example.kkbackend.repositories.MovieRepository;
import com.example.kkbackend.repositories.RateRepository;
import com.example.kkbackend.service.MemberService;
import com.example.kkbackend.service.RateService;
import jakarta.persistence.EntityNotFoundException;
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
    private final RateService rateService;
    private final MemberRepository memberRepository;

    @PostMapping
    public RateDto postRate(@RequestBody RateDto rateDto) {
        var movie = movieRepository.getReferenceById(UUID.fromString(rateDto.movieId()));
        Member member;
        try {
            member = memberService.getMemberByUsername(rateDto.username());
        } catch (EntityNotFoundException ex) {
            member = memberRepository.save(Member.builder()
                    .freshBlood(true)
                    .userName(rateDto.username())
                    .build());
        }
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

    @RequestMapping("/delete")
    @PostMapping
    public boolean deleteRate(@RequestParam String rateId) {
        try{
            UUID uuid = UUID.fromString(rateId);
            if (rateRepository.existsById(uuid)) {
                rateRepository.deleteById(uuid);
                return true;
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @PutMapping
    public RateDto putRate(@RequestBody RateDto rateDto) {
        var movie = movieRepository.getReferenceById(UUID.fromString(rateDto.movieId()));
        var member = memberService.getMemberByUsername(rateDto.username());
        var rate = rateRepository.getReferenceById(UUID.fromString(rateDto.id()));
        return fromRateToDto(rateRepository.save(fromDtoToRate(rateDto, member, movie)));
    }

    @GetMapping("/average/{movieId}")
    public AverageRateDto getAverage(@PathVariable String movieId) {
        var movie = movieRepository.getReferenceById(UUID.fromString(movieId));
        return AverageRateDto.builder()
                .movieName(movie.getName())
                .rating(countAverageRating(movie))
                .build();
    }

    @GetMapping("/{movieId}/{username}")
    public RateDto getRateByMovieAndUsername(@PathVariable String movieId,
                                             @PathVariable String username) {
        return fromRateToDto(rateService.getRateByMovieIdAndUsername(UUID.fromString(movieId), username));
    }

    private int countAverageRating(Movie movie) {
        var ratings = movie.getRatings();
        if (ratings.isEmpty()) {
            return 0;
        }
        return ratings.stream().map(Rate::getRating).mapToInt(Integer::intValue).sum() / ratings.size();
    }

    public static RateDto fromRateToDto(Rate rate) {
        return RateDto.builder()
                .id(rate.getId().toString())
                .rating(rate.getRating())
                .liked(rate.isLiked())
                .discussable(rate.isDiscussable())
                .username(rate.getMember().getUserName())
                .movieId(rate.getMovie().getId().toString())
                .build();
    }

    public static Rate fromDtoToRate(RateDto dto, Member member, Movie movie) {
        return Rate.builder()
                .id(UUID.fromString(dto.id()))
                .rating(dto.rating())
                .liked(dto.liked())
                .discussable(dto.discussable())
                .member(member)
                .movie(movie)
                .build();
    }
}
