package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.*;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.entities.Movie;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.service.EventService;
import com.example.kkbackend.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final MovieService movieService;

    @PostMapping
    public EventDto postEvent(@RequestBody CreateEventDto createEventDto) {
        var movie = movieService.getById(createEventDto.movieId());
        var event = eventRepository.save(fromDtoToEvent(createEventDto, movie));
        return fromEventToDto(event);
    }

    @GetMapping()
    public EventDto getLatestEvent() {
        var event = eventRepository.findFirstByOrderByDateDesc();
        return fromEventToDto(event);
    }

    @GetMapping("/movies")
    public List<EventMovieDto> getAllEventsWithMoviesInfo(
            @RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        if (pageNumber == null || pageSize == null) {
            pageNumber = 0;
            pageSize = 10;
        }

        return eventRepository.findAllByOrderByDateDesc(PageRequest.of(pageNumber, pageSize)).getContent().stream()
                //remove 2 years anniversary party event
                .filter(e -> !Objects.equals(e.getDate().toString(),
                        "2025-01-12")
                )
                .map(e ->
                    EventMovieDto.builder()
                        .movie(MovieController.fromMovieToDto(e.getMovie()))
                        .language(e.getLanguage())
                        .date(e.getDate().toString())
                        .build()
                ).toList();
    }

    public static EventDto fromEventToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
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
                .description(event.getDescription())
                .posterUrl(event.getPosterUrl())
                .build();
    }

    public static Event fromDtoToEvent(CreateEventDto createEventDto, Movie movie) {
        return Event.builder()
                .movie(movie)
                .language(createEventDto.language())
                .date(java.sql.Date.valueOf(createEventDto.date()))
                .members(new HashSet<>())
                .telegramMessages(new ArrayList<>())
                .posterUrl(createEventDto.posterUrl())
                .build();
    }
}
