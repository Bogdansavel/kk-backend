package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.*;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.entities.Member;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.repositories.MemberRepository;
import com.example.kkbackend.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    @Value("${current-event}")
    private String currentEvent;

    @GetMapping("members")
    public List<MemberDto> getMembers() {
        return eventRepository.getReferenceById(UUID.fromString(currentEvent)).getMembers()
                .stream()
                .map(MemberMapper::toDto)
                .toList();
    }

    @PostMapping("register")
    @Transactional
    public RegisterResponseDto register(@RequestBody RegisterDto registerDto) {
        var member = memberService.getMemberByTelegramIdOrFirstNameOrUsername(
                registerDto.telegramId(), registerDto.firstName(), registerDto.username());
        member = member.map(memberRepository::save)
                .or(() -> Optional.of(memberRepository.save(MemberMapper.toModel(registerDto))));

        var event = eventRepository.getReferenceById(UUID.fromString(currentEvent));
        if (event.getMembers().contains(member.get())) {
            return RegisterResponseDto.builder().isAlreadyRegistered(true).build();
        }
        if (event.getMembers().size() >= 16) {
            return RegisterResponseDto.builder().limitIsExceeded(true).build();
        }
        event.getMembers().add(member.get());
        member.get().getEvents().add(event);
        if (member.get().isFreshBlood() && member.get().getEvents().size() > 1) {
            member.get().setFreshBlood(false);
            memberRepository.save(member.get());
        }
        return registerResponseDtoFromEvent(eventRepository.save(event));
    }

    @PostMapping("unregister")
    @Transactional
    public RegisterResponseDto unregister(@RequestBody RegisterDto registerDto) {
        var member = memberService.getMemberByTelegramIdOrFirstNameOrUsername(
                registerDto.telegramId(), registerDto.firstName(), registerDto.username());
        if (member.isEmpty()) {
            member = Optional.of(MemberMapper.toModel(registerDto));
        }

        var event = eventRepository.getReferenceById(UUID.fromString(currentEvent));

        if (!event.getMembers().contains(member.get())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "registration not found"
            );
        }
        event.getMembers().remove(member.get());
        member.get().getEvents().remove(event);
        return registerResponseDtoFromEvent(eventRepository.save(event));
    }

    @PostMapping("/register/batch")
    public EventDto registerBatch(@RequestBody RegisterBatchDto registerBatchDto) {
        var eventOptional = eventRepository.findById(UUID.fromString(registerBatchDto.eventId()));
        if (eventOptional.isEmpty()) {
            throw new EntityNotFoundException(
                    MessageFormat.format("Event with id {0} dosen't exist!", registerBatchDto.eventId()));
        }
        for (var username : registerBatchDto.usernames()) {
            var memberOptional = memberRepository.getMemberByUserName(username);
            if (memberOptional.isPresent()) {
                eventOptional.get().getMembers().add(memberOptional.get());
                memberOptional.get().getEvents().add(eventOptional.get());
            }
        }
        return EventController.fromEventToDto(eventRepository.save(eventOptional.get()));
    }

    private RegisterResponseDto registerResponseDtoFromEvent(Event event) {
        return RegisterResponseDto.builder()
                .membersCount(event.getMembers().size())
                .isAlreadyRegistered(false)
                .messages(event.getTelegramMessages()
                        .stream()
                        .map(m -> TelegramMessageDto.builder()
                                .messageId(m.getMessageId())
                                .chatId(m.getChatId())
                                .build()).collect(Collectors.toList()))
                .members(event.getMembers()
                        .stream()
                        .map(MemberMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
