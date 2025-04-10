package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.CreateTelegramMessageDto;
import com.example.kkbackend.dtos.CreateTelegramMessageForRoundDto;
import com.example.kkbackend.dtos.TelegramMessageDto;
import com.example.kkbackend.entities.TelegramMessage;
import com.example.kkbackend.repositories.EventRepository;
import com.example.kkbackend.repositories.RoundRepository;
import com.example.kkbackend.repositories.TelegramMessageRepository;
import com.example.kkbackend.service.RoundService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/telegram-message")
@RequiredArgsConstructor
public class TelegramMessageController {
    @Value("${current-event}")
    private String currentEvent;

    private final TelegramMessageRepository telegramMessageRepository;
    private final EventRepository eventRepository;
    private final RoundRepository roundRepository;

    private final RoundService roundService;

    @PostMapping
    @Transactional
    public void postMessage(@RequestBody CreateTelegramMessageDto createTelegramMessageDto) {
        var event = eventRepository.getById(UUID.fromString(currentEvent));
        var message = TelegramMessage.builder()
                .messageId(createTelegramMessageDto.messageId())
                .chatId(createTelegramMessageDto.chatId())
                .event(event)
                .build();
        telegramMessageRepository.save(message);
    }

    @PostMapping("/round")
    @Transactional
    public void postMessageForRound(@RequestBody CreateTelegramMessageForRoundDto createTelegramMessageForRoundDto) {
        var round = roundService.getById(createTelegramMessageForRoundDto.roundId());

        var message = TelegramMessage.builder()
                .messageId(createTelegramMessageForRoundDto.messageId())
                .chatId(createTelegramMessageForRoundDto.chatId())
                .event(null)
                .build();

        round.setMessage(telegramMessageRepository.save(message));
        roundRepository.save(round);
    }

    public static TelegramMessageDto fromModelToDto(TelegramMessage telegramMessage) {
        return TelegramMessageDto.builder()
                .chatId(telegramMessage.getChatId())
                .messageId(telegramMessage.getMessageId())
                .build();
    }
}
