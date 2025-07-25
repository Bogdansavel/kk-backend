package com.example.kkbackend.controllers;

import com.example.kkbackend.dtos.CreateRoundDto;
import com.example.kkbackend.dtos.RoundDto;
import com.example.kkbackend.dtos.SetReadyDto;
import com.example.kkbackend.dtos.TelegramMessageDto;
import com.example.kkbackend.entities.Round;
import com.example.kkbackend.repositories.RoundRepository;
import com.example.kkbackend.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/round")
@RequiredArgsConstructor
public class RoundController {
    private final RoundRepository roundRepository;
    private final RoundService roundService;

    @PostMapping()
    public RoundDto postRound(@RequestBody CreateRoundDto createRoundDto) {
        return fromRoundToDto(roundRepository.save(new Round(createRoundDto.id(), createRoundDto.isActive())));
    }

    @PutMapping("/prepare/last")
    public RoundDto prepareLastActiveRoundMovies() {
        return fromRoundToDto(roundService.prepareLastActiveRound());
    }

    @PutMapping("/prepare/previous")
    public RoundDto preparePreviousActiveRoundMovies() {
        return fromRoundToDto(roundService.preparePreviousActiveRound());
    }

    @PostMapping("/setReady")
    public RoundDto setReady(@RequestBody SetReadyDto setReadyDto) {
        return fromRoundToDto(roundService.setReady(setReadyDto.telegramId(), setReadyDto.isReady()));
    }

    @GetMapping()
    public RoundDto getLastActiveRound() {
        return fromRoundToDto(roundService.getLastActiveRound());
    }

    public static RoundDto fromRoundToDto(Round round) {
        TelegramMessageDto message = null;
        TelegramMessageDto pollMessage = null;
        if (round.getMessage() != null) {
            message = TelegramMessageController.fromModelToDto(round.getMessage());
        }

        if (round.getPollMessage() != null) {
            pollMessage = TelegramMessageController.fromModelToDto(round.getPollMessage());
        }

        return RoundDto.builder()
                .id(round.getId())
                .movies(round.getMovies().stream().map(MovieController::fromMovieToDto)
                        .collect(Collectors.toSet()))
                .message(message)
                .pollMessage(pollMessage)
                .build();
    }
}
