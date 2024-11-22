package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.*;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.repositories.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.stream.Collectors;
import com.example.kkbackend.entities.Member;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterController {
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
        var member = memberRepository.getMemberByFirstName(registerDto.firstName());
        if (member.isEmpty()) {
            member = memberRepository.getMemberByUserName(registerDto.username());
            if (member.isEmpty()) {
                member = Optional.of(memberRepository.save(MemberMapper.toModel(registerDto)));
            } else {
                var memberValue = member.get();
                memberValue.setFirstName(registerDto.firstName());
                member = Optional.of(memberRepository.save(memberValue));
            }
        }
        var event = eventRepository.getReferenceById(UUID.fromString(currentEvent));
        if (event.getMembers().contains(member.get())) {
            return RegisterResponseDto.builder().isAlreadyRegistered(true).build();
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
        var member = memberRepository.getMemberByUserName(registerDto.username());
        if (member.isEmpty()) {
            member = Optional.of(memberRepository.save(
                    Member.builder()
                            .userName(registerDto.username())
                            .events(new HashSet<>())
                            .build()));
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
                        .map(m -> MemberDto.builder()
                                .username(m.getUserName())
                                .freshBlood(m.isFreshBlood())
                                .build()).collect(Collectors.toList()))
                .build();
    }
}
