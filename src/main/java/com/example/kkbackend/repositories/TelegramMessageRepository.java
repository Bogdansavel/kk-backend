package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.TelegramMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TelegramMessageRepository extends JpaRepository<TelegramMessage, UUID> {
}
