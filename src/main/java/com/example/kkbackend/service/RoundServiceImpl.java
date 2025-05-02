package com.example.kkbackend.service;

import com.example.kkbackend.entities.Round;
import com.example.kkbackend.exception.ActiveRoundsCountException;
import com.example.kkbackend.repositories.RoundRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class RoundServiceImpl implements RoundService {
    private final RoundRepository roundRepository;
    private final MemberService memberService;

    @Override
    public Round getById(long id) {
        return roundRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MessageFormat.format("Round number {0} doesn't exist!", id)));
    }

    @Override
    public Round getActiveRound() {
        var activeRounds = roundRepository.findByIsActiveTrue();
        if (activeRounds.isEmpty()) {
            throw new ActiveRoundsCountException("There is no active rounds yet!");
        }
        if (activeRounds.size() > 1) {
            throw new ActiveRoundsCountException("There are more than one active rounds!");
        }
        return activeRounds.get(0);
    }

    @Override
    @Transactional
    public Round prepare() {
        var round = getActiveRound();
        var movies = round.getMovies();
        movies.forEach(movie -> movie.setIsReady(null));
        return roundRepository.save(round);
    }

    @Override
    @Transactional
    public Round setReady(double telegramId, boolean isReady) {
        var member = memberService.getByTelegramId(telegramId);
        var round = getActiveRound();
        for(var movie : round.getMovies()) {
            if (movie.getMember().getId().equals(member.getId())) {
                movie.setIsReady(isReady);
            }
        }
        return roundRepository.save(round);
    }
}
