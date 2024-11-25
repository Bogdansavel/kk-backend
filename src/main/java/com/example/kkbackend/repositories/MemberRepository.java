package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> getMemberByUserName(String userName);
    Optional<Member> getMemberByFirstName(String firstName);
    Optional<Member> getMemberByTelegramIdOrUserNameOrFirstName(Integer telegramId, String userName, String firstName);
}
