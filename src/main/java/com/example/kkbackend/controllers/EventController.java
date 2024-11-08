package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.CreateEventDto;
import com.example.kkbackend.dtos.EventDto;
import com.example.kkbackend.dtos.MemberDto;
import com.example.kkbackend.dtos.TelegramMessageDto;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
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
                        .map(m -> MemberDto.builder()
                                .username(m.getUserName())
                                .freshBlood(m.isFreshBlood())
                                .build())
                        .collect(Collectors.toList())
                )
                .build();
    }
}
