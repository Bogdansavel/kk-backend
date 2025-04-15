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

    @PutMapping("/prepare")
    public RoundDto prepareRoundMovies() {
        return fromRoundToDto(roundService.prepare());
    }

    @PostMapping("/setReady")
    public RoundDto setReady(@RequestBody SetReadyDto setReadyDto) {
        return fromRoundToDto(roundService.setReady(setReadyDto.telegramId(), setReadyDto.isReady()));
    }

    @GetMapping()
    public RoundDto getActiveRound() {
        return fromRoundToDto(roundService.getActiveRound());
    }

    public static RoundDto fromRoundToDto(Round round) {
        TelegramMessageDto message = null;
        if (round.getMessage() != null) {
            message = TelegramMessageController.fromModelToDto(round.getMessage());
        }

        return RoundDto.builder()
                .id(round.getId())
                .movies(round.getMovies().stream().map(MovieController::fromMovieToDto)
                        .collect(Collectors.toSet()))
                .message(message)
                .build();
    }
}
