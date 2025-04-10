package com.example.kkbackend.service;

import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.entities.Rate;
import com.example.kkbackend.repositories.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final RoundService roundService;
    private final MemberService memberService;

    @Override
    public Movie getById(UUID id) {
        var movie = movieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                MessageFormat.format("Movie with id {0} doesn't exist!", id)));
        movie.setRatings(movie.getRatings().stream()
                .sorted(Comparator.comparing(Rate::isCommented, Comparator.reverseOrder()))
                .toList());
        return movie;
    }

    @Override
    @Transactional
    public Movie postMovie(Movie movie, long roundId, UUID memberId) {
        movie.setRound(roundService.getById(roundId));
        movie.setMember(memberService.getById(memberId));
        return movieRepository.save(movie);
    }
}
