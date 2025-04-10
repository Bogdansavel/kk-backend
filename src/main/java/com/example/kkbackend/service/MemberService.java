package com.example.kkbackend.service;

import com.example.kkbackend.entities.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberService {
    Member getMemberByUsername(String username);
    Member getById(UUID id);
    Optional<Member> getMemberByTelegramIdOrFirstNameOrUsername(double telegramId, String firstName, String username);
    Member getByTelegramId(double telegramId);
}
