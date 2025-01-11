package com.example.kkbackend.controllers;

import com.example.kkbackend.client.KinopoiskDbClient;
import com.example.kkbackend.dtos.MovieWithKinopoiskDataDto;
import com.example.kkbackend.dtos.WrappedDto;
import com.example.kkbackend.dtos.kinopoiskData.KPCountry;
import com.example.kkbackend.dtos.kinopoiskData.KPGenre;
import com.example.kkbackend.dtos.kinopoiskData.KPPerson;
import com.example.kkbackend.dtos.kinopoiskData.KinopoiskData;
import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.mapper.MemberMapper;
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
        var topMovies = movies.stream().sorted(Comparator.comparingInt(Movie::averageRating).reversed());

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
            if (genres.containsKey(genre)) {
                genres.replace(genre, genres.get(genre) + 1);
            } else {
                genres.put(genre, 1);
            }
        });
        var  topGenres =  genres.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        var countries = new HashMap<String, Integer>();
        moviesData.stream().map(KinopoiskData::countries).flatMap(Set::stream).map(KPCountry::name).forEach(country -> {
            if (countries.containsKey(country)) {
                countries.replace(country, countries.get(country) + 1);
            } else {
                countries.put(country, 1);
            }
        });
        var topCountries = countries.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        var persons = new HashMap<KPPerson, Integer>();
        moviesData.stream().map(KinopoiskData::persons).flatMap(Set::stream)
                .filter(person -> person.profession().equals("актеры"))
                .forEach(person -> {
            if (persons.containsKey(person)) {
                persons.replace(person, persons.get(person) + 1);
            } else {
                persons.put(person, 1);
            }
        });
        var topPersons = persons.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(5).toList();

        var topArtiestMovies = moviesData.stream()
                .filter(
                data -> data.persons().stream()
                        .map(KPPerson::id).anyMatch(id -> id.equals(topPersons.get(0).getKey().id())))
                .map(KinopoiskData::id)
                .toList();

        var directors = new HashMap<KPPerson, Integer>();
        moviesData.stream().map(KinopoiskData::persons).flatMap(Set::stream)
                .filter(person -> person.profession().equals("режиссеры"))
                .forEach(person -> {
                    if (directors.containsKey(person)) {
                        directors.replace(person, directors.get(person) + 1);
                    } else {
                        directors.put(person, 1);
                    }
                });
        var topDirectors = directors.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        return new WrappedDto(
                movies.size() * 5,
                moviesData.stream().map(KinopoiskData::movieLength).mapToInt(Integer::intValue).sum(),
                movies.size(),
                movies.stream().filter(m -> m.getRatings().stream().map(r -> r.getMember().getId())
                        .anyMatch(id -> memberOptional.get().getId().equals(id))).count(),
                topMovies.limit(3).map(MovieController::fromMovieToDto).toList(),
                topGenres.limit(5).toList(),
                topCountries.toList(),
                moviesData.stream().map(KinopoiskData::persons).flatMap(Set::stream)
                        .filter(person -> person.profession().equals("актеры")).map(KPPerson::id).distinct().count(),
                topPersons,
                movies.stream().filter(m -> topArtiestMovies.contains(m.getKinopoiskId()))
                        .map(MovieController::fromMovieToDto).toList(),
                moviesData.stream().map(KinopoiskData::persons).flatMap(Set::stream)
                        .filter(person -> person.profession().equals("режиссеры")).map(KPPerson::id).distinct().count(),
                topDirectors.limit(5).toList());
    }
}
