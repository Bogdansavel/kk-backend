package com.example.kkbackend.repositories;

import com.example.kkbackend.entities.RegistrationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationInfoRepository extends JpaRepository<RegistrationInfo, UUID> {
    Optional<RegistrationInfo> getRegistrationInfoByContact(String contact);
}
