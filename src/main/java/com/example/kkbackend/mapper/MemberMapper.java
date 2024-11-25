package com.example.kkbackend.mapper;

import com.example.kkbackend.dtos.MemberDto;
import com.example.kkbackend.dtos.RegisterDto;
import com.example.kkbackend.entities.Event;
import com.example.kkbackend.entities.Member;
import com.example.kkbackend.entities.Rate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MemberMapper {
    public static MemberDto toDto(Member member) {
        return MemberDto.builder()
                .telegramId(member.getTelegramId())
                .username(member.getUserName())
                .firstName(member.getFirstName())
                .freshBlood(member.isFreshBlood())
                .build();
    }

    public static Member toModel(MemberDto memberDto, Set<Event> events, List<Rate> ratings) {
        return Member.builder()
                .telegramId(memberDto.telegramId())
                .userName(memberDto.username())
                .firstName(memberDto.firstName())
                .freshBlood(memberDto.freshBlood())
                .events(events)
                .ratings(ratings)
                .build();
    }

    public static Member toModel(RegisterDto registerDto) {
        return Member.builder()
                .telegramId(registerDto.telegramId())
                .userName(registerDto.username())
                .firstName(registerDto.firstName())
                .freshBlood(true)
                .events(new HashSet<>())
                .ratings(new LinkedList<>())
                .build();
    }
}
