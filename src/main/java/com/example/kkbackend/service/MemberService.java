package com.example.kkbackend.service;

import com.example.kkbackend.dtos.RegisterDto;
import com.example.kkbackend.entities.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberService {
    Member getMemberByUsername(String username);
    Member getById(UUID id);
    Member getMemberByTelegramIdOrUsername(Optional<Double> telegramId, Optional<String> username);
    Member getByTelegramId(double telegramId);
    Member getOrCreate(RegisterDto registerDto);
}
