package com.example.kkbackend.service;

import com.example.kkbackend.dtos.RegisterDto;
import com.example.kkbackend.entities.Member;
import com.example.kkbackend.mapper.MemberMapper;
import com.example.kkbackend.repositories.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
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

    public Member getMemberByTelegramIdOrUsername(
            Optional<Double> telegramId, Optional<String> username) {
        var member = getOptionalMemberByTelegramIdOrUsername(telegramId, username);
        if (member.isEmpty()) {
            var errorMessages = new ArrayList<String>();
            if (telegramId.isEmpty()) {
                errorMessages.add("telegramId = " + telegramId);
            }
            if (username.isEmpty()) {
                errorMessages.add("username = " + username);
            }
            throw new EntityNotFoundException("There is no member with " + String.join(", ", errorMessages));
        }
        return member.get();
    }

    private Optional<Member> getOptionalMemberByTelegramIdOrUsername(
            Optional<Double> telegramId, Optional<String> username) {
        if (telegramId.isEmpty() && username.isEmpty()) {
            throw new IllegalArgumentException("Provide at least one field for searching member!");
        }

        Optional<Member> member = Optional.empty();
        if (telegramId.isPresent()) {
            member = memberRepository.getMemberByTelegramId(telegramId.get());
        }
        if (member.isEmpty() && username.isPresent()) {
            var usernameValue = username.get();
            usernameValue = trimUsername(usernameValue);
            member = memberRepository.getMemberByUserName(usernameValue);
        }
        return member;
    }

    private String trimUsername(String username) {
        if (username.charAt(0) == '@') {
            username = username.substring(1);
        }
        return username;
    }

    @Override
    public Member getByTelegramId(double telegramId) {
        return memberRepository.getMemberByTelegramId(telegramId)
                .orElseThrow(() -> new EntityNotFoundException(
                    MessageFormat.format("Member with telegram id {0} doesn't exist!", telegramId)));
    }

    @Override
    public Member getOrCreate(RegisterDto registerDto) {
        var member = getOptionalMemberByTelegramIdOrUsername(
                Optional.of(registerDto.telegramId()), Optional.ofNullable(registerDto.firstName())); //searching by 2 fields because initially I was storing members my username which is turned to be optional by telegram API
        if (member.isPresent()) {
            updateUsersInfo(member.get(),
                    Optional.ofNullable(registerDto.firstName()),
                    Optional.ofNullable(registerDto.username()));
            return member.get();
        } else {
            return memberRepository.save(MemberMapper.toModel(registerDto));
        }
    }

    private Member updateUsersInfo(Member member, Optional<String> firstName, Optional<String> username) {
        var isFirstNameChanged = !Optional.ofNullable(member.getFirstName()).equals(firstName) && firstName.isPresent();
        var isUserNameChanged = !member.getUserName().equals(username) && username.isPresent();

        if (isFirstNameChanged) {
            member.setFirstName(firstName.get());
        }
        if (isUserNameChanged) {
            member.setUserName(username.get());
        }

        if (isFirstNameChanged || isUserNameChanged) {
            return memberRepository.save(member);
        }
        return member;
    }
}
