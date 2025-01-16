package com.example.kkbackend.dtos;

import com.example.kkbackend.dtos.kinopoiskData.KPPerson;

import java.util.List;

public record KPPersonEntry (
    KPPerson person, List<MovieDto> movies
) {}
