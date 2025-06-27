package com.example.kkbackend.service;

import com.example.kkbackend.entities.Round;
import com.example.kkbackend.exception.ActiveRoundsCountException;
import com.example.kkbackend.repositories.RoundRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

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

    private List<Round> getActiveRounds() {
        var activeRounds = roundRepository.findByIsActiveTrueOrderByIdDesc();
        if (activeRounds.isEmpty()) {
            throw new ActiveRoundsCountException("There is no active rounds yet!");
        }
        return activeRounds;
    }

    @Override
    public Round getLastActiveRound() {
        return getActiveRounds().get(0);
    }

    @Override
    public Round getPreviousActiveRound() {
        var activeRounds = getActiveRounds();
        if (activeRounds.size() < 2) {
            throw new ActiveRoundsCountException("There is only one active round!");
        }
        return getActiveRounds().get(1);
    }

    @Override
    @Transactional
    public Round prepare(Round round) {
        var movies = round.getMovies();
        movies.forEach(movie -> movie.setIsReady(null));
        return roundRepository.save(round);
    }

    @Override
    @Transactional
    public Round preparePreviousActiveRound() {
        return prepare(getPreviousActiveRound());
    }

    @Override
    @Transactional
    public Round prepareLastActiveRound() {
        return prepare(getLastActiveRound());
    }

    @Override
    @Transactional
    public Round setReady(double telegramId, boolean isReady) {
        var member = memberService.getByTelegramId(telegramId);
        var round = getLastActiveRound();
        for(var movie : round.getMovies()) {
            if (movie.getMember().getId().equals(member.getId())) {
                movie.setIsReady(isReady);
            }
        }
        return roundRepository.save(round);
    }
}
