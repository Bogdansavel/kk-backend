package com.example.kkbackend.service;

import com.example.kkbackend.entities.Member;
import com.example.kkbackend.repositories.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    public Member getMemberByUsername(String username) {
        return memberRepository.getMemberByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("Member with username {0} is not existed!", username)));
    }

    public Optional<Member> getMemberByUsernameOrFirtsName(String firstName, String username) {
            var member = memberRepository.getMemberByUserName(username);
            if (member.isEmpty()) {
                member = memberRepository.getMemberByFirstName(firstName);
            }
        return member;
    }

    public Optional<Member> getMemberByTelegramIdOrFirstNameOrUsername(double telegramId, String firstName, String username) {
        var member = memberRepository.getMemberByTelegramId(telegramId);
        if (member.isEmpty()) {
            member = memberRepository.getMemberByUserName(username);
            if (member.isEmpty()) {
                member = memberRepository.getMemberByFirstName(firstName);
            }
            member.get().setTelegramId(telegramId);
            memberRepository.save(member.get());
        }
        return member;
    }
}
