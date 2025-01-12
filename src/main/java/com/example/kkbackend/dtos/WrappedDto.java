package com.example.kkbackend.dtos;

import com.example.kkbackend.dtos.kinopoiskData.KPGenre;
import com.example.kkbackend.dtos.kinopoiskData.KPPerson;
import com.example.kkbackend.dtos.kinopoiskData.KinopoiskData;

import java.util.List;
import java.util.Map;

public record WrappedDto (
        long eventsCount,
        String firstEvent,
        long visitedEventsCount,
        int topVisitorsPlace,
        List<Map.Entry<MemberDto, Integer>> topVisitors,
        int streak,
        int topStreaksPlace,
        List<Map.Entry<MemberDto, Integer>> topStreaks,
        long eventYouMissedCount,
        int offeredByYou,
        List<Map.Entry<MemberDto, Integer>> topOffers,
        int allEventsTime,
        int allMoviesTime,
        int moviesCount,
        long moviesRatedCount,
        int movieRatedPlace,
        List<Map.Entry<MemberDto, Long>> topRates,
        List<MovieDto> topMovies,
        List<MovieDto> worstMovies,
        List<Map.Entry<String, Integer>> topGenres,
        List<Map.Entry<String, Integer>> countries,
        long actorsCount,
        List<Map.Entry<KPPerson, Integer>> topPersons,
        List<MovieDto> topArtistMovies,
        KinopoiskData oldestMovie,
        KinopoiskData newestMovie
) {
}
