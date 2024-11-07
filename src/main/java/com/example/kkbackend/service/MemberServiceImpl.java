package com.example.kkbackend.service;

import com.example.kkbackend.entities.Member;
import com.example.kkbackend.repositories.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    public Member getMemberByUsername(String username) {
        return memberRepository.getMemberByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("Member with username {0} is not existed!", username)));
    }
}
