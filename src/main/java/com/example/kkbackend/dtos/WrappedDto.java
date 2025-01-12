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
        List<MemberDtoEntry> topVisitors,
        int streak,
        int topStreaksPlace,
        List<MemberDtoEntry> topStreaks,
        long eventYouMissedCount,
        int offeredByYou,
        List<MemberDtoEntry> topOffers,
        int allEventsTime,
        int allMoviesTime,
        int moviesCount,
        long moviesRatedCount,
        int movieRatedPlace,
        List<MemberDtoEntry> topRates,
        List<MovieDto> topMovies,
        List<MovieDto> worstMovies,
        List<StringEntry> topGenres,
        List<StringEntry> countries,
        long actorsCount,
        List<KPPersonEntry> topPersons,
        List<MovieDto> topArtistMovies,
        KinopoiskData oldestMovie,
        KinopoiskData newestMovie
) {
}
