package com.example.kkbackend.dtos;

import com.example.kkbackend.dtos.kinopoiskData.KPGenre;
import com.example.kkbackend.dtos.kinopoiskData.KPPerson;
import com.example.kkbackend.dtos.kinopoiskData.KinopoiskData;

import java.util.List;
import java.util.Map;

public record WrappedDto (
        int allEventsTime,
        int allMoviesTime,
        int moviesCount,
        long moviesRated,
        List<MovieDto> topMovies,
        List<Map.Entry<String, Integer>> topGenres,
        List<Map.Entry<String, Integer>> countries,
        long actorsCount,
        List<Map.Entry<KPPerson, Integer>> topPersons,
        List<MovieDto> topArtistMovies,
        long directorsCount,
        List<Map.Entry<KPPerson, Integer>> topDirectors
) {
}
