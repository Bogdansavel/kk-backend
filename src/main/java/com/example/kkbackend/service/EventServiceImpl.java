package com.example.kkbackend.service;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.entities.Member;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.repositories.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final MemberService memberService;

    @Override
    public Event getLatest() {
        return eventRepository.findFirstByOrderByDateDesc();
    }

    @Override
    public Event addMember(Event event, Member member) {
        event.getMembers().add(member);
        member.getEvents().add(event);
        if (member.isFreshBlood() && member.getEvents().size() > 1) {
            member.setFreshBlood(false);
        }
        return eventRepository.save(event);
    }

    @Override
    public Event addMembers(Event event, List<Member> members) {
        for (var member : members) {
            event.getMembers().add(member);
            member.getEvents().add(event);
        }

        return eventRepository.save(event);
    }

    @Override
    public Event removeMember(Event event, Member member) {
        if (!event.getMembers().contains(member)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "registration not found"
            );
        }
        event.getMembers().remove(member);
        member.getEvents().remove(event);
        return eventRepository.save(event);
    }

    @Override
    public Event getById(UUID id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MessageFormat.format("Event with id {0} doesn't exist!", id))
        );
    }
}
