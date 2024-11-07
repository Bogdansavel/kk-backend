package com.example.kkbackend.service;

import com.example.kkbackend.entities.Rate;
import com.example.kkbackend.repositories.MovieRepository;
import com.example.kkbackend.repositories.RateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {
    private final RateRepository rateRepository;
    private final MemberService memberService;
    private final MovieRepository movieRepository;

    @Override
    public Rate getRateByMovieIdAndUsername(UUID movieId, String username) {
        return rateRepository.getRateByMovieAndMember(
                movieRepository.getReferenceById(movieId),
                        memberService.getMemberByUsername(username))
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("Rate with id {0} and username {1} is not existed!",
                                movieId, username)));
    }
}
