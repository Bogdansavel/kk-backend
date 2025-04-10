package com.example.kkbackend.service;

import com.example.kkbackend.entities.Round;

public interface RoundService {
    Round getById(long id);
    Round getActiveRound();
    Round prepareRound(Round round);
    Round setReady(double telegramId, boolean isReady);
}
