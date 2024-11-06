package com.example.kkbackend.service;

import com.example.kkbackend.entities.Member;

public interface MemberService {
    Member getMemberByUsername(String username);
}
