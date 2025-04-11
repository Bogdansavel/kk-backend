package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.MessageDto;
import com.example.kkbackend.entities.TelegramMessage;
import com.example.kkbackend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("message")
@RequiredArgsConstructor
public class MessageController {
    private final EventService eventService;

    @GetMapping("current")
    public List<MessageDto> getMessages() {
        return eventService.getLatest().getTelegramMessages()
                .stream()
                .map(this::fromTelegramMessageToDto)
                .collect(Collectors.toList());
    }

    private MessageDto fromTelegramMessageToDto(TelegramMessage message) {
        return MessageDto.builder()
                .id(message.getId().toString())
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .eventId(message.getEvent().getId().toString())
                .build();
    }
}
