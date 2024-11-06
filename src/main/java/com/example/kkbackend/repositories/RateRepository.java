package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RateRepository extends JpaRepository<Rate, UUID> {
}
