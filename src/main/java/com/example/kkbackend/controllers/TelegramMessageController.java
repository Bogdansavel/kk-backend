package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.CreateTelegramMessageDto;
import com.example.kkbackend.dtos.CreateTelegramMessageForRoundDto;
import com.example.kkbackend.dtos.TelegramMessageDto;
import com.example.kkbackend.entities.TelegramMessage;
import com.example.kkbackend.repositories.RoundRepository;
import com.example.kkbackend.repositories.TelegramMessageRepository;
import com.example.kkbackend.service.EventService;
import com.example.kkbackend.service.RoundService;
import com.example.kkbackend.service.TelegramMessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegram-message")
@RequiredArgsConstructor
public class TelegramMessageController {
    private final TelegramMessageRepository telegramMessageRepository;
    private final RoundRepository roundRepository;

    private final RoundService roundService;
    private final EventService eventService;
    private final TelegramMessageService telegramMessageService;

    @PostMapping
    @Transactional
    public void postMessage(@RequestBody CreateTelegramMessageDto createTelegramMessageDto) {
        var event = eventService.getLatest();
        var message = TelegramMessage.builder()
                .messageId(createTelegramMessageDto.messageId())
                .chatId(createTelegramMessageDto.chatId())
                .event(event)
                .build();
        telegramMessageRepository.save(message);
    }

    @PostMapping("/poll")
    @Transactional
    public boolean postPollMessage(@RequestBody CreateTelegramMessageDto createTelegramMessageDto) {
        return telegramMessageService.createPollMessage(fromCreateDtoToModel(createTelegramMessageDto));
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

    public static TelegramMessage fromCreateDtoToModel(CreateTelegramMessageDto createTelegramMessageDto) {
        return TelegramMessage.builder()
                .chatId(createTelegramMessageDto.chatId())
                .messageId(createTelegramMessageDto.messageId())
                .build();
    }
}
