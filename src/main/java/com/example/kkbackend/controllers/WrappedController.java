package com.example.kkbackend.controllers;

import com.example.kkbackend.client.KinopoiskDbClient;
import com.example.kkbackend.dtos.*;
import com.example.kkbackend.dtos.kinopoiskData.KPCountry;
import com.example.kkbackend.dtos.kinopoiskData.KPGenre;
import com.example.kkbackend.dtos.kinopoiskData.KPPerson;
import com.example.kkbackend.dtos.kinopoiskData.KinopoiskData;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.entities.Member;
import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.entities.Rate;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.repositories.MemberRepository;
import com.example.kkbackend.repositories.MovieRepository;
import com.example.kkbackend.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wrapped")
@RequiredArgsConstructor
public class WrappedController {
    private final KinopoiskDbClient kinopoiskDbClient;
    private final MovieRepository movieRepository;
    private final MemberService memberService;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    @PutMapping("/update/movie/{movieId}")
    @Transactional
    public MovieWithKinopoiskDataDto updateKinopoiskData(@PathVariable String movieId) {
        var movieOptional = movieRepository.findById(UUID.fromString(movieId));
        if (movieOptional.isPresent()) {
            var movie = movieOptional.get();
            var jsonData = kinopoiskDbClient.getMovieByKinopoiskId(movieOptional.get().getKinopoiskId());
            movie.setKinopoiskData(jsonData);
            movieRepository.save(movie);
            return MovieController.fromMovieToWithKinopoiskDataDto(movie);
        }
        throw new EntityNotFoundException(
                MessageFormat.format("Movie with movie id {0} doesn't exist!", movieId));
    }

    @PutMapping("/update/movie/all")
    @Transactional
    public boolean updateKinopoiskData() {
        var movies = movieRepository.findAll();
        movies.stream()
                //exclude 2 years anniversary entity
                .filter(movie -> !movie.getId().equals(UUID.fromString("408536e4-9a1a-4eb7-87bf-fc5d77d8326f")))
                .forEach(movie ->
                movie.setKinopoiskData(
                        kinopoiskDbClient.getMovieByKinopoiskId(movie.getKinopoiskId())));
        movieRepository.saveAll(movies);
        return true;
    }

    @GetMapping("/{telegramId}/{userName}/{firstName}")
    public WrappedDto generateWrapped(@PathVariable String userName, @PathVariable String firstName,
                                @PathVariable String telegramId) {
        var memberOptional = memberService.getMemberByTelegramIdOrFirstNameOrUsername(
                Integer.parseInt(telegramId), firstName, userName);
        if (memberOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    MessageFormat.format("There is no member with telegramId {0}, username {1}, firstName {2}",
                            telegramId, userName, firstName));
        }

        var movies = movieRepository.findAll();
        var topMovies = movies.stream().sorted(Comparator.comparingInt(Movie::averageRating).reversed()).toList();
        var worstMovies = movies.stream().sorted(Comparator.comparingInt(Movie::averageRating)).toList();

        var mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
        var moviesData = movies.stream()
                .filter(movie -> movie.getKinopoiskData() != null)
                .map(movie -> {
                    try {
                        return mapper.readValue(movie.getKinopoiskData(), KinopoiskData.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

        var genres = new HashMap<String, Integer>();
        moviesData.stream().map(KinopoiskData::genres).flatMap(Set::stream).map(KPGenre::name).forEach(genre -> {
            genres.merge(genre, 1, Integer::sum);
        });
        var  topGenres =  genres.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        var countries = new HashMap<String, Integer>();
        moviesData.stream().map(KinopoiskData::countries).flatMap(Set::stream).map(KPCountry::name).forEach(country -> {
            countries.merge(country, 1, Integer::sum);
        });
        var topCountries = countries.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        var persons = new HashMap<KPPerson, Integer>();
        moviesData.stream().map(KinopoiskData::persons).flatMap(Set::stream)
                .filter(person -> person.profession().equals("актеры"))
                .forEach(person -> {
            persons.merge(person, 1, Integer::sum);
        });
        var topPersons = persons.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(3).toList();

        var topArtiestMovies = moviesData.stream()
                .filter(
                data -> data.persons().stream()
                        .map(KPPerson::id).anyMatch(id -> id.equals(topPersons.get(0).getKey().id())))
                .map(KinopoiskData::id)
                .toList();

        var offers = new HashMap<Member, Integer>();
        movies.stream().map(Movie::getMember).filter(Objects::nonNull)
                .forEach(member -> offers.merge(member, 1, Integer::sum));
        var topOffers = offers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        var ratesPlaces = new HashMap<Member, Long>();
        movies.stream().map(Movie::getRatings).flatMap(List::stream).map(Rate::getMember).distinct().forEach(m ->{
            ratesPlaces.put(m,
                    movies.stream()
                            .map(Movie::getRatings)
                            .flatMap(List::stream)
                            .filter(r -> r.getMember().getId().equals(m.getId())).count());
        });
        var topRates = ratesPlaces.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();

        int topRatesPlace = 0;
        for (var entry : topRates) {
            topRatesPlace++;
            if (entry.getKey().equals(memberOptional.get())) break;
        }

        var yourEventsDates = memberOptional.get().getEvents().stream().map(Event::getDate).sorted().toList();
        var events = eventRepository.findAll();
        var allEventsDates = events.stream().map(Event::getDate).sorted().toList();

        int streak = 0;
        int streak2 = 0;
        for (var date : allEventsDates) {
            if (yourEventsDates.contains(date)) streak2++;
            else {
                if (streak2 > streak) streak = streak2;
                streak2 = 0;
            }
        }

        var members = memberRepository.findAll();
        var visits = new HashMap<MemberDto, Integer>();
        members.forEach(m -> visits.put(MemberMapper.toDto(m), m.getEvents().size()));
        var topVisits = visits.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();
        var topVisitorsPlace = 0;
        for (var entry : topVisits) {
            topVisitorsPlace++;
            if (entry.getKey().equals(MemberMapper.toDto(memberOptional.get()))) break;
        }

        var streaks = new HashMap<MemberDto, Integer>();
        members.forEach(m -> {
            var allMembersEvents = m.getEvents().stream().map(Event::getDate).toList();
            int s = 0;
            int s2 = 0;
            for (var date : allEventsDates) {
                if (allMembersEvents.contains(date)) s2++;
                else {
                    if (s2 > s) s = s2;
                    s2 = 0;
                }
            }
            streaks.put(MemberMapper.toDto(m), s);
        });
        var topStreaks = streaks.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();
        var topStreaksPlace = 0;
        for (var entry : topStreaks) {
            topStreaksPlace++;
            if (entry.getKey().equals(MemberMapper.toDto(memberOptional.get()))) break;
        }


        var eventsAfterYourVisit = allEventsDates.stream()
                .filter(d -> d.after(yourEventsDates.get(0)) || d.equals(yourEventsDates.get(0))).toList();
        var eventsYouMissed = new ArrayList<>(eventsAfterYourVisit);
        eventsYouMissed.removeIf(yourEventsDates::contains);

        var years = moviesData.stream().sorted(Comparator.comparingInt(KinopoiskData::year)).toList();

        return new WrappedDto(
                events.size(),
                yourEventsDates.get(0).toString(),
                yourEventsDates.size(),
                topVisitorsPlace,
                topVisits.stream().limit(3).map(e -> new MemberDtoEntry(e.getKey(), e.getValue())).toList(),
                streak,
                topStreaksPlace,
                topStreaks.stream().limit(3).map(e -> new MemberDtoEntry(e.getKey(), e.getValue())).toList(),
                eventsYouMissed.size(),
                offers.get(memberOptional.get()),
                topOffers.limit(6).map(entry ->
                        Map.entry(MemberMapper.toDto(entry.getKey()), entry.getValue())
                ).map(e -> new MemberDtoEntry(e.getKey(), e.getValue())).toList(),
                movies.size() * 5,
                moviesData.stream().map(KinopoiskData::movieLength).mapToInt(Integer::intValue).sum(),
                movies.size(),
                movies.stream().filter(m -> m.getRatings().stream().map(r -> r.getMember().getId())
                        .anyMatch(id -> memberOptional.get().getId().equals(id))).count(),
                topRatesPlace,
                topRates.stream().map(entry ->
                        Map.entry(MemberMapper.toDto(entry.getKey()), entry.getValue().intValue())
                ).limit(3).map(e -> new MemberDtoEntry(e.getKey(), e.getValue())).toList(),
                topMovies.stream().limit(3).map(MovieController::fromMovieToDto).toList(),
                worstMovies.stream().limit(3).map(MovieController::fromMovieToDto).toList(),
                topGenres.limit(5).map(e -> new StringEntry(e.getKey(), e.getValue())).toList(),
                topCountries.map(e -> new StringEntry(e.getKey(), e.getValue())).toList(),
                moviesData.stream().map(KinopoiskData::persons).flatMap(Set::stream)
                        .filter(person -> person.profession().equals("актеры")).map(KPPerson::id).distinct().count(),
                topPersons.stream().map(e -> new KPPersonEntry(e.getKey(), e.getValue())).toList(),
                movies.stream().filter(m -> topArtiestMovies.contains(m.getKinopoiskId()))
                        .map(MovieController::fromMovieToDto).toList(),
                years.get(0),
                years.get(years.size()-1)
        );
    }
}
