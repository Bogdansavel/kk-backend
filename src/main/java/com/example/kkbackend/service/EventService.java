package com.example.kkbackend.service;

import com.example.kkbackend.dtos.CreateEventDto;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.entities.Member;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public interface EventService {
    Event createEvent(CreateEventDto createEventDto);
    Event getLatest();
    Event addMember(Event event, Member member);
    Event addMembers(Event event, List<Member> members);
    Event removeMember(Event event, Member member);
    Event getById(UUID id);
    void deleteById(UUID id);
    Event stop(UUID id);
    List<Event> findAllEventsByMovieNameWithAllDetails(String titlePattern, PageRequest pageRequest);
}
