package com.example.kkbackend.dtos;

import com.example.kkbackend.dtos.kinopoiskData.KPPerson;

public record KPPersonEntry (
    KPPerson person, int value
) {}
