package com.example.kkbackend.service;

import com.example.kkbackend.entities.Member;

import java.util.Optional;

public interface MemberService {
    Member getMemberByUsername(String username);
    Optional<Member> getMemberByTelegramIdOrFirstNameOrUsername(int telegramId, String firstName, String username);
}
