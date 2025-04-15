package com.example.kkbackend.service;

import com.example.kkbackend.entities.Round;

public interface RoundService {
    Round getById(long id);
    Round getActiveRound();
    Round prepare();
    Round setReady(double telegramId, boolean isReady);
}
