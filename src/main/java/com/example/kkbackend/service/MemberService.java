package com.example.kkbackend.service;

import com.example.kkbackend.entities.Member;

import java.util.Optional;

public interface MemberService {
    Member getMemberByUsername(String username);
    Optional<Member> getMemberByTelegramIdOrFirstNameOrUsername(double telegramId, String firstName, String username);
}
