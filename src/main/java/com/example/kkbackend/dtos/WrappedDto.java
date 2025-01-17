package com.example.kkbackend.dtos;
import com.example.kkbackend.dtos.kinopoiskData.KinopoiskData;

import java.util.List;

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
        List<CountyEntryDto> countries,
        List<CountyEntryDto> ages,
        long actorsCount,
        List<KPPersonEntry> topPersons,
        KinopoiskData oldestMovie,
        KinopoiskData newestMovie,
        List<MovieDto> controverses,
        double allRatingsAvg,
        double myRatingsAvg
) {
}
