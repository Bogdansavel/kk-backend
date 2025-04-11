package com.example.kkbackend.service;

import com.example.kkbackend.entities.Event;
import com.example.kkbackend.entities.Member;

import java.util.List;
import java.util.UUID;

public interface EventService {
    Event getLatest();
    Event addMember(Event event, Member member);
    Event addMembers(Event event, List<Member> members);
    Event removeMember(Event event, Member member);
    Event getById(UUID id);
}
