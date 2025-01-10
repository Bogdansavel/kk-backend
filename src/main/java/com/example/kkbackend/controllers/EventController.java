package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.*;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventRepository eventRepository;
    private final MovieRepository movieRepository;

    @Value("${current-event}")
    private String currentEvent;

    @PostMapping
    public EventDto postEvent(@RequestBody CreateEventDto createEventDto) {
        var movie = movieRepository.getById(createEventDto.movieId());
        var event = eventRepository.save(
                Event.builder()
                        .movie(movie)
                        .language(createEventDto.language())
                        .date(java.sql.Date.valueOf(createEventDto.date()))
                        .members(new HashSet<>())
                        .telegramMessages(new ArrayList<>())
                        .build());
        return fromEventToDto(event);
    }

    @GetMapping
    public EventDto getEvent() {
        var event = eventRepository.getById(UUID.fromString(currentEvent));
        return fromEventToDto(event);
    }

    @GetMapping("/movies")
    public List<EventMovieDto> getAllEventsWithMoviesInfo() {
        return eventRepository.findAll().stream()
                //remove 2 years anniversary party event
                .filter(e -> !Objects.equals(e.getMovie().getId().toString(),
                        "f8de891e-0aa6-44fb-8f13-4dfe23440248")
                )
                .map(e ->
                    EventMovieDto.builder()
                        .movie(MovieController.fromMovieToDto(e.getMovie()))
                        .language(e.getLanguage())
                        .date(e.getDate().toString())
                        .build()
                ).toList();
    }

    private EventDto fromEventToDto(Event event) {
        return EventDto.builder()
                .movieId(event.getMovie().getId().toString())
                .language(event.getLanguage())
                .date(event.getDate().toString())
                .messages(event.getTelegramMessages()
                        .stream()
                        .map(m -> TelegramMessageDto.builder()
                                .chatId(m.getChatId())
                                .messageId(m.getMessageId())
                                .build()).collect(Collectors.toList()))
                .members(event.getMembers()
                        .stream()
                        .map(MemberMapper::toDto)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
