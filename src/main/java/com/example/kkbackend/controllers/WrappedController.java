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
import com.example.kkbackend.service.RoundService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@RequestMapping("/wrapped")
@RequiredArgsConstructor
public class WrappedController {
    private final KinopoiskDbClient kinopoiskDbClient;
    private final MovieRepository movieRepository;
    private final RoundService roundService;
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

    @PutMapping("/update/movie/lastActiveRound")
    @Transactional
    public boolean updateKinopoiskDataForLastActiveRound() {
        var round = roundService.getLastActiveRound();
        var movies = round.getMovies();
        movies.stream()
                //exclude 2 years anniversary entity
                .filter(movie -> !movie.getId().equals(UUID.fromString("408536e4-9a1a-4eb7-87bf-fc5d77d8326f")))
                .forEach(movie ->
                        movie.setKinopoiskData(kinopoiskDbClient.getMovieByKinopoiskId(movie.getKinopoiskId())));
        movieRepository.saveAll(movies);
        return true;
    }

    @PutMapping("/update/movie/all")
    @Transactional
    public boolean updateKinopoiskData() {
        var movies = movieRepository.findAll();
        movies.stream()
                //exclude 2 years anniversary entity
                .filter(movie -> !movie.getId().equals(UUID.fromString("408536e4-9a1a-4eb7-87bf-fc5d77d8326f")))
                .forEach(movie ->
                movie.setKinopoiskData(kinopoiskDbClient.getMovieByKinopoiskId(movie.getKinopoiskId())));
        movieRepository.saveAll(movies);
        return true;
    }

    @GetMapping("{userName}")
    public WrappedDto generateWrapped(@PathVariable String userName) throws ParseException {
        var member = memberService.getMemberByUsername(userName);

        var events = eventRepository.findAllByDateBetween(
                LocalDate.of(2025, Month.JANUARY, 12),
                LocalDate.of(2026, Month.JANUARY, 10)
        );

        var movies = events.stream().map(Event::getMovie).filter(Objects::nonNull).toList();
        var topMovies = movies
                .stream()
                .sorted(Comparator.comparingInt(Movie::averageRating).reversed())
                .toList();
        var worstMovies = movies.stream().sorted(Comparator.comparingInt(Movie::averageRating)).toList();

        var moviesKpId = new HashMap<Integer, MovieDto>();
        movies.forEach(movie -> moviesKpId.put(movie.getKinopoiskId(), MovieController.fromMovieToDto(movie)));

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

        var persons = new HashMap<KPPerson, List<MovieDto>>();
        moviesData.forEach(movie -> {
                    movie.persons().stream()
                    .filter(person -> person.profession().equals("актеры"))
                    .forEach(person -> {
                        if (persons.containsKey(person)) {
                            persons.get(person).add(moviesKpId.get(movie.id()));
                        } else {
                            var list = new ArrayList<MovieDto>();
                            list.add(moviesKpId.get(movie.id()));
                            persons.put(person, list);
                        }});
                });
        var topPersons = new ArrayList<>(persons.entrySet().stream()
                .sorted(Comparator.comparingInt(o -> o.getValue().size())).toList());
        Collections.reverse(topPersons);

        var directors = new HashMap<KPPerson, List<MovieDto>>();
        moviesData.forEach(movie -> {
            movie.persons().stream()
                    .filter(person -> person.profession().equals("режиссеры"))
                    .forEach(person -> {
                        if (directors.containsKey(person)) {
                            directors.get(person).add(moviesKpId.get(movie.id()));
                        } else {
                            var list = new ArrayList<MovieDto>();
                            list.add(moviesKpId.get(movie.id()));
                            directors.put(person, list);
                        }});
        });
        var topDirectors = new ArrayList<>(directors.entrySet().stream()
                .sorted(Comparator.comparingInt(o -> o.getValue().size())).toList());
        Collections.reverse(topDirectors);

        var offers = new HashMap<Member, Integer>();
        movies.stream().map(Movie::getMember).filter(Objects::nonNull)
                .forEach(m -> offers.merge(m, 1, Integer::sum));
        var topOffers = offers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        var offersCount = Optional.ofNullable(offers.get(member)).orElse(0);

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
            if (entry.getKey().equals(member)) break;
        }

        var countries = new HashMap<String, List<MovieDto>>();
        moviesData
                .forEach(movie ->
                    movie.countries().stream().map(KPCountry::name).forEach(country -> {
                        if (countries.containsKey(country)) {
                            countries.get(country).add(moviesKpId.get(movie.id()));
                        } else {
                            var list = new ArrayList<MovieDto>();
                            list.add(moviesKpId.get(movie.id()));
                            countries.put(country, list);
                        }})
                );
        var topCountries = new ArrayList<>(countries.entrySet().stream()
                .sorted(Comparator.comparingInt(o -> o.getValue().size())).toList());
        Collections.reverse(topCountries);

        var ages = new HashMap<Integer, List<MovieDto>>();
        for (int age = 1920; age <= 2020; age+=10) {
            ages.put(age, new ArrayList<>());
        }
        moviesData
                .forEach(movie ->
                        ages.forEach((key, value) -> {
                            if (movie.year() < key + 10 && movie.year() > key) {
                                ages.get(key).add(moviesKpId.get(movie.id()));
                            }
                        })
                );
        var topAges = new ArrayList<>(ages.entrySet().stream()
                .sorted(Comparator.comparingInt(o -> o.getValue().size()))
                .filter(e -> !e.getValue().isEmpty()).toList());
        Collections.reverse(topAges);

        var yourEventsDates = events.stream()
                .filter(e -> e.getMembers().contains(member))
                .map(Event::getDate)
                .sorted()
                .toList();
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
        members.forEach(m -> visits.put(MemberMapper.toDto(m),
                m.getEvents().stream().filter(events::contains).toList().size()));

        var topVisits = visits.entrySet().stream()
                .filter(e -> e.getValue() != 0)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();
        var topVisitorsPlace = 0;
        for (var entry : topVisits) {
            topVisitorsPlace++;
            if (entry.getKey().equals(MemberMapper.toDto(member))) break;
        }

        var streaks = new HashMap<MemberDto, Integer>();
        members.forEach(m -> {
            var allMembersEvents = m.getEvents().stream().filter(events::contains).map(Event::getDate).toList();
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
            if (entry.getKey().equals(MemberMapper.toDto(member))) break;
        }

        List<LocalDate> eventsAfterYourVisit = new ArrayList<>();
        if (!yourEventsDates.isEmpty()) {
            eventsAfterYourVisit = allEventsDates.stream()
                    .filter(d -> d.isAfter(yourEventsDates.get(0)) || d.equals(yourEventsDates.get(0))).toList();
        }
        var eventsYouMissed = new ArrayList<>(eventsAfterYourVisit);
        eventsYouMissed.removeIf(yourEventsDates::contains);

        var years = moviesData.stream().sorted(Comparator.comparingInt(KinopoiskData::year)).toList();

        var controverses = movies.stream().sorted(Comparator.comparingInt(Movie::getControversy).reversed())
                .limit(6).map(MovieController::fromMovieToDto).map(
                        m ->
                             new MovieDto(
                                    m.id(), m.kinopoiskId(), m.name(),
                                    m.ratings().stream().sorted(Comparator.comparingInt(RateDto::rating).reversed())
                                            .toList(),
                                    m.ratePhotoName(), m.posterUrl(), m.averageRating(), m.member(), null, null, null
                            )
                ).toList();

        var ratingsStats = movies.stream()
                .map(Movie::getRatings).flatMap(List::stream).map(Rate::getRating)
                .mapToInt(Integer::intValue).summaryStatistics();

        var myRatingsStats = member.getRatings().stream().map(Rate::getRating)
                .mapToInt(Integer::intValue).summaryStatistics();

        var result = new WrappedDto(
                events.size(),
                getFirstVisitedEventDate(yourEventsDates),
                yourEventsDates.size(),
                topVisitorsPlace,
                topVisits.stream().map(e -> new MemberDtoEntry(e.getKey(), e.getValue())).toList(),
                streak,
                topStreaksPlace,
                topStreaks.stream().filter(v -> v.getValue() != 0).map(e -> new MemberDtoEntry(e.getKey(), e.getValue())).toList(),
                eventsYouMissed.size(),
                offersCount,
                topOffers.map(entry ->
                        Map.entry(MemberMapper.toDto(entry.getKey()), entry.getValue())
                ).map(e -> new MemberDtoEntry(e.getKey(), e.getValue())).toList(),
                movies.size() * 5,
                moviesData.stream().map(KinopoiskData::movieLength).mapToInt(Integer::intValue).sum(),
                movies.size(),
                movies.stream().filter(m -> m.getRatings().stream().map(r -> r.getMember().getId())
                        .anyMatch(id -> member.getId().equals(id))).count(),
                topRatesPlace,
                topRates.stream().map(entry ->
                        Map.entry(MemberMapper.toDto(entry.getKey()), entry.getValue().intValue())
                ).map(e -> new MemberDtoEntry(e.getKey(), e.getValue())).toList(),
                topMovies.stream().filter(e -> e.averageRating() > 0).limit(3).map(MovieController::fromMovieToDto).toList(),
                topMovies.stream().filter(e -> e.averageRating() > 0).map(MovieController::fromMovieToDto).toList(),
                worstMovies.stream().filter(e -> e.averageRating() > 0).limit(3).map(MovieController::fromMovieToDto).toList(),
                worstMovies.stream().filter(e -> e.averageRating() > 0).map(MovieController::fromMovieToDto).toList(),
                calculateGenres(moviesData),
                topCountries.stream().map(c -> new CountyEntryDto(c.getKey(), c.getValue())).toList(),
                topAges.stream().map(a -> new CountyEntryDto(a.getKey().toString(), a.getValue())).toList(),
                moviesData.stream().map(KinopoiskData::persons).flatMap(Set::stream)
                        .filter(person -> person.profession().equals("актеры")).map(KPPerson::id).distinct().count(),
                topPersons.stream().filter(e -> e.getValue().size() > 1).map(e -> new KPPersonEntry(e.getKey(), e.getValue())).limit(12).toList(),
                topDirectors.stream().filter(e -> e.getValue().size() > 1).map(e -> new KPPersonEntry(e.getKey(), e.getValue())).limit(12).toList(),
                years.get(0),
                years.get(years.size()-1),
                controverses,
                ratingsStats.getAverage(),
                myRatingsStats.getAverage()
        );

        return result;
    }

    private List<StringEntry> calculateGenres(List<KinopoiskData> moviesData) {
        var genres = new HashMap<String, Integer>();
        moviesData.stream().map(KinopoiskData::genres).flatMap(Set::stream).map(KPGenre::name).forEach(genre -> {
            genres.merge(genre, 1, Integer::sum);
        });
       return genres.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(5).map(e -> new StringEntry(e.getKey(), e.getValue())).toList();
    }

    private String getFirstVisitedEventDate(List<LocalDate> yourEventsDates) {
        if (yourEventsDates.isEmpty()) return "";
        else return yourEventsDates.get(0).toString();
    }
}
