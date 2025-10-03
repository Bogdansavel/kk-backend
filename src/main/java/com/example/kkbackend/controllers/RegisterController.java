package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.*;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.service.EventService;
import com.example.kkbackend.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterController {
    private final MemberService memberService;
    private final EventService eventService;

    @GetMapping("members")
    public List<MemberDto> getMembers() {
        return eventService.getLatest().getMembers()
                .stream()
                .map(MemberMapper::toDto)
                .toList();
    }

    @PostMapping("register")
    @Transactional
    public RegisterResponseDto register(@RequestBody RegisterDto registerDto) {
        var member = memberService.getOrCreate(registerDto);
        var event = eventService.getById(UUID.fromString(registerDto.eventId()));
        //manshort exclusive check
        var event1 = eventService.getById(UUID.fromString("8d7f269e-20f9-4d62-8d07-5329ebf74cb8"));
        var event2 = eventService.getById(UUID.fromString("b6a92295-883d-48a2-8099-9545dd2c2549"));

        if (event1.getMembers().contains(member) || event2.getMembers().contains(member)) {
            return RegisterResponseDto.builder().isAlreadyRegistered(true).build();
        }
        if (event.getMembers().size() >= 16) {
            return RegisterResponseDto.builder().limitIsExceeded(true).build();
        }

        return registerResponseDtoFromEvent(eventService.addMember(event, member));
    }

    @PostMapping("unregister")
    @Transactional
    public RegisterResponseDto unregister(@RequestBody RegisterDto registerDto) {
        var member = memberService.getOrCreate(registerDto);
        var event = eventService.getById(UUID.fromString(registerDto.eventId()));

        return registerResponseDtoFromEvent(eventService.removeMember(event, member));
    }

    @PostMapping("/register/batch")
    public EventDto registerBatch(@RequestBody RegisterBatchDto registerBatchDto) {
        var event = eventService.getById(UUID.fromString(registerBatchDto.eventId()));
        var members = Arrays.stream(registerBatchDto.usernames()).map(memberService::getMemberByUsername).toList();

        return EventController.fromEventToDto(eventService.addMembers(event, members));
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
