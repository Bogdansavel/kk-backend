package com.example.kkbackend.service;

import com.example.kkbackend.entities.Round;

public interface RoundService {
    Round getById(long id);
    Round getLastActiveRound();
    Round getPreviousActiveRound();
    Round prepare(Round round);
    Round prepareLastActiveRound();
    Round preparePreviousActiveRound();
    Round setReady(double telegramId, boolean isReady);
}
