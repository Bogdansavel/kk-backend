package com.example.kkbackend.service;

import com.example.kkbackend.dtos.RegisterDto;
import com.example.kkbackend.entities.Member;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.repositories.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public Member getById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("Member with id {0} doesn't exist!", id)));
    }

    public Member getMemberByUsername(String username) {
        return memberRepository.getMemberByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("Member with username {0} doesn't exist!", username)));
    }

    public Optional<Member> getMemberByUsernameOrFirtsName(String firstName, String username) {
            var member = memberRepository.getMemberByUserName(username);
            if (member.isEmpty()) {
                member = memberRepository.getMemberByFirstName(firstName);
            }
        return member;
    }

    public Optional<Member> getMemberByTelegramIdOrFirstNameOrUsername(double telegramId, String firstName,
                                                                       String username) {
        var member = memberRepository.getMemberByTelegramId(telegramId);
        if (member.isEmpty()) {
            member = memberRepository.getMemberByUserName(username);
            if (member.isEmpty()) {
                member = memberRepository.getMemberByFirstName(firstName);
                if (member.isEmpty()) {
                    return Optional.empty();
                }
            }
            member.get().setTelegramId(telegramId);
            member = Optional.of(memberRepository.save(member.get()));
        }
        member = Optional.of(updateUsersInfo(member.get(), firstName, username));
        return member;
    }

    @Override
    public Member getByTelegramId(double telegramId) {
        return memberRepository.getMemberByTelegramId(telegramId)
                .orElseThrow(() -> new EntityNotFoundException(
                    MessageFormat.format("Member with telegram id {0} doesn't exist!", telegramId)));
    }

    @Override
    public Member getOrSave(RegisterDto registerDto) {
        var member = getMemberByTelegramIdOrFirstNameOrUsername(
                registerDto.telegramId(), registerDto.firstName(), registerDto.username());
        return member.orElse(memberRepository.save(MemberMapper.toModel(registerDto)));
    }

    private Member updateUsersInfo(Member member, String firstName, String username) {
        if (member.getFirstName() == null) {
            member.setFirstName("");
        }

        if (member.getUserName() == null) {
            member.setUserName("");
        }

        var firstNameHasBeenUpdated = !member.getFirstName().equals(firstName);
        var userNameHasBeenUpdated = !member.getUserName().equals(username);
        if (firstNameHasBeenUpdated) {
            member.setFirstName(firstName);
        }
        if (userNameHasBeenUpdated) {
            member.setUserName(username);
        }
        if (firstNameHasBeenUpdated || userNameHasBeenUpdated) {
            return memberRepository.save(member);
        }
        return member;
    }
}
