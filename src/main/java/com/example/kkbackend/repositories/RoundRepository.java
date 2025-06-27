package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findByIsActiveTrueOrderByIdDesc();
}
