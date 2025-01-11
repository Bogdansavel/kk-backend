package com.example.kkbackend.dtos.kinopoiskData;

import lombok.Builder;

import java.util.Set;

@Builder
public record KinopoiskData(
    int id,
    String name,
    String type,
    int year,
    String description,
    String shortDescription,
    KPRating rating,
    int movieLength,
    String ratingMpaa,
    KPPoster poster,
    KPBackDrop backdrop,
    Set<KPGenre> genres,
    Set<KPCountry> countries,
    Set<KPPerson> persons,
    KPLogo logo
) {}
